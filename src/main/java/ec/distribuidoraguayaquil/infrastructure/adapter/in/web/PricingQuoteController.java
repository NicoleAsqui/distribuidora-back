package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.QuoteAdminMailService;
import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.OrderItem;
import ec.distribuidoraguayaquil.domain.port.in.AuthUseCase;
import ec.distribuidoraguayaquil.domain.port.in.OrderUseCase;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import java.math.RoundingMode;
import java.util.ArrayList;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.PricingQuoteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.PricingQuoteJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pricing-quotes")
public class PricingQuoteController {

    private final PricingQuoteJpaRepository repository;
    private final ObjectMapper objectMapper;
    private final QuoteAdminMailService quoteAdminMailService;
    private final OrderUseCase orderUseCase;
    private final AuthUseCase authUseCase;

    public PricingQuoteController(
            PricingQuoteJpaRepository repository,
            ObjectMapper objectMapper,
            QuoteAdminMailService quoteAdminMailService,
            OrderUseCase orderUseCase,
            AuthUseCase authUseCase) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.quoteAdminMailService = quoteAdminMailService;
        this.orderUseCase = orderUseCase;
        this.authUseCase = authUseCase;
    }

    /**
     * Cotización aceptada por el cliente → pedido (estado Pendiente, origen "cotizacion").
     * Luego el admin lo pasa a Pagado con fecha y abono y entra al calendario.
     * body opcional: { fulfillmentMethod, address, city, references, mapsUrl, paymentMethod }
     */
    @PostMapping("/{id}/to-order")
    public Order toOrder(@PathVariable String id, @RequestBody(required = false) Map<String, Object> body,
                         HttpServletRequest http) {
        PricingQuoteEntity q = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (q.getOrderCode() != null && !q.getOrderCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esta cotización ya es el pedido " + q.getOrderCode() + ".");
        }
        Map<String, Object> extra = body == null ? Map.of() : body;
        List<OrderItem> items = quoteItems(q);
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cotización no tiene productos.");
        }
        ObjectNode c = objectMapper.createObjectNode();
        c.put("source", "cotizacion");
        c.put("quoteId", q.getId());
        c.put("quoteCode", q.getCode());
        c.put("fullName", asString(q.getClientName()));
        c.put("phone", asString(q.getClientPhone()));
        c.put("email", asString(q.getClientEmail()));
        c.put("requiresInvoice", Boolean.TRUE.equals(q.getRequiresInvoice()));
        String fm = asString(extra.get("fulfillmentMethod"));
        c.put("fulfillmentMethod", fm.isBlank() ? "pickup" : fm);
        c.put("deliveryZone", "guayaquil");
        c.put("city", asString(extra.getOrDefault("city", "Guayaquil")));
        c.put("address", asString(extra.get("address")));
        c.put("references", asString(extra.get("references")));
        c.put("mapsUrl", asString(extra.get("mapsUrl")));
        c.put("paymentMethod", asString(extra.get("paymentMethod")));
        if (q.getDeliveryDate() != null && q.getDeliveryDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            c.put("estimatedDate", q.getDeliveryDate());
        }
        String checkoutJson;
        try {
            checkoutJson = objectMapper.writeValueAsString(c);
        } catch (JacksonException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos inválidos");
        }
        String notes = "Desde cotización " + q.getCode()
                + (q.getNotes() == null || q.getNotes().isBlank() ? "" : "\n" + q.getNotes().trim());
        Order draft = new Order(null, null,
                asString(q.getClientName()).isBlank() ? "Cliente" : q.getClientName(),
                q.getClientEmail(), q.getClientPhone(), notes, checkoutJson, items, null, null, null);
        Order order = orderUseCase.createFromQuote(draft, q.getTotal(), actor(http), q.getCode());
        q.setOrderCode(order.code());
        q.setStatus("accepted");
        repository.save(q);
        return order;
    }

    /** Líneas de la cotización (simple o de motor) → ítems de pedido con su precio unitario. */
    private List<OrderItem> quoteItems(PricingQuoteEntity q) {
        List<OrderItem> out = new ArrayList<>();
        try {
            JsonNode arr = objectMapper.readTree(q.getItemsJson() == null ? "[]" : q.getItemsJson());
            if (!arr.isArray()) {
                return out;
            }
            for (JsonNode it : arr) {
                int qty = Math.max(1, it.path("cantidad").asInt(1));
                JsonNode pricing = it.path("pricing");
                BigDecimal line = dec(pricing.path("lineTotal"));
                if (line.signum() == 0) {
                    line = dec(it.path("lineTotal"));
                }
                BigDecimal unit = dec(pricing.path("precioUnidad"));
                if (unit.signum() == 0) {
                    unit = dec(it.path("precioUnitario"));
                }
                if (unit.signum() == 0 && line.signum() > 0) {
                    unit = line.divide(BigDecimal.valueOf(qty), 2, RoundingMode.HALF_UP);
                }
                String disenoNombre = it.path("disenoNombre").asText("");
                String label = it.path("label").asText("");
                String name = !disenoNombre.isBlank() ? disenoNombre : (label.isBlank() ? "Producto" : label);
                String size = "";
                if (it.has("largo")) {
                    size = fmt(it.path("largo")) + "×" + fmt(it.path("ancho")) + "×" + fmt(it.path("altoBase")) + " cm";
                }
                String color = it.path("material").asText("");
                String image = it.path("imageUrl").asText("");
                out.add(new OrderItem(q.getCode(), name, size, color,
                        unit.setScale(2, RoundingMode.HALF_UP), qty, image.isBlank() ? null : image));
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudieron leer los productos de la cotización.");
        }
        return out;
    }

    private static BigDecimal dec(JsonNode n) {
        if (n == null || n.isMissingNode() || n.isNull()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(n.asText("0"));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private static String fmt(JsonNode n) {
        BigDecimal v = dec(n).stripTrailingZeros();
        return v.signum() == 0 ? "0" : v.toPlainString();
    }

    private String actor(HttpServletRequest http) {
        String auth = http.getHeader("Authorization");
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : http.getHeader("X-Admin-Token");
        String user = authUseCase.usernameFromToken(token);
        if (user == null || user.isBlank()) {
            return "Admin";
        }
        return Character.toUpperCase(user.charAt(0)) + user.substring(1);
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return repository.findAllOrderedByCreatedAtDesc().stream().map(this::toMap).toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable String id) {
        return repository.findById(id).map(this::toMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, Object> body) {
        PricingQuoteEntity e = fromBody(body, true);
        e.setEditToken(UUID.randomUUID().toString());
        PricingQuoteEntity saved = repository.save(e);
        quoteAdminMailService.notifyAdminNewWebQuote(saved);
        Map<String, Object> map = toMap(saved);
        // Cliente web: no devolver precios en la respuesta
        if ("web".equalsIgnoreCase(saved.getSource())) {
            return publicClientView(map);
        }
        return map;
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        PricingQuoteEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        body.put("id", id);
        body.putIfAbsent("code", existing.getCode());
        body.putIfAbsent("createdAt", existing.getCreatedAt().toString());
        body.putIfAbsent("source", existing.getSource());
        body.putIfAbsent("clientName", existing.getClientName());
        body.putIfAbsent("clientPhone", existing.getClientPhone());
        body.putIfAbsent("clientEmail", existing.getClientEmail());
        body.putIfAbsent("deliveryDate", existing.getDeliveryDate());
        body.putIfAbsent("status", existing.getStatus());
        body.putIfAbsent("notes", existing.getNotes());
        body.putIfAbsent("kind", existing.getKind());
        body.putIfAbsent("requiresInvoice", existing.getRequiresInvoice());
        body.putIfAbsent("subtotal", existing.getSubtotal());
        body.putIfAbsent("iva", existing.getIva());
        PricingQuoteEntity e = fromBody(body, false);
        e.setEditToken(ensureEditToken(existing));
        e.setOrderCode(existing.getOrderCode());
        return toMap(repository.save(e));
    }

    /** Desde el correo: cargar cotización completa (con precios) con token. */
    @GetMapping("/public-edit/{id}")
    public Map<String, Object> getForPublicEdit(@PathVariable String id, @RequestParam String token) {
        PricingQuoteEntity e = requireByEditToken(id, token);
        return toMap(e);
    }

    /** Desde el correo: actualizar solo ítems/precios/total; conserva cliente y medidas. */
    @PutMapping("/public-edit/{id}")
    public Map<String, Object> savePublicEdit(
            @PathVariable String id,
            @RequestParam String token,
            @RequestBody Map<String, Object> body) {
        PricingQuoteEntity existing = requireByEditToken(id, token);
        try {
            Object items = body.get("items");
            existing.setItemsJson(objectMapper.writeValueAsString(items == null ? List.of() : items));
        } catch (JacksonException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items inválidos");
        }
        Object total = body.get("total");
        if (total != null) {
            existing.setTotal(new BigDecimal(String.valueOf(total)));
        }
        if (body.containsKey("notes")) {
            existing.setNotes(asString(body.get("notes")));
        }
        return toMap(repository.save(existing));
    }

    private PricingQuoteEntity requireByEditToken(String id, String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }
        PricingQuoteEntity e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String expected = e.getEditToken();
        if (expected == null || expected.isBlank() || !expected.equals(token.trim())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Enlace de edición inválido");
        }
        return e;
    }

    private static String ensureEditToken(PricingQuoteEntity existing) {
        if (existing.getEditToken() != null && !existing.getEditToken().isBlank()) {
            return existing.getEditToken();
        }
        return UUID.randomUUID().toString();
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        PricingQuoteEntity e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        e.setStatus(body.getOrDefault("status", e.getStatus()));
        return toMap(repository.save(e));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        repository.deleteById(id);
    }

    /** Envía la cotización simple al email del cliente (PDF en base64). */
    @PostMapping("/{id}/email-client")
    public Map<String, Object> emailClient(
            @PathVariable String id,
            @RequestBody Map<String, Object> body) {
        PricingQuoteEntity e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String pdfBase64 = asString(body.get("pdfBase64"));
        String filename = asString(body.get("filename"));
        try {
            quoteAdminMailService.sendManualQuoteToClient(e, pdfBase64, filename);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo enviar el correo");
        }
        return Map.of(
                "ok", true,
                "to", e.getClientEmail() == null ? "" : e.getClientEmail(),
                "code", e.getCode()
        );
    }

    private PricingQuoteEntity fromBody(Map<String, Object> body, boolean isNew) {
        PricingQuoteEntity e = new PricingQuoteEntity();
        String id = body.get("id") == null || String.valueOf(body.get("id")).isBlank()
                ? UUID.randomUUID().toString()
                : String.valueOf(body.get("id"));
        e.setId(id);
        String code = body.get("code") == null || String.valueOf(body.get("code")).isBlank()
                ? "COT-" + System.currentTimeMillis() % 1_000_000
                : String.valueOf(body.get("code"));
        e.setCode(code);
        e.setSource(String.valueOf(body.getOrDefault("source", "web")));
        if (isNew || body.get("createdAt") == null) {
            e.setCreatedAt(Instant.now());
        } else {
            e.setCreatedAt(Instant.parse(String.valueOf(body.get("createdAt"))));
        }
        e.setClientName(asString(body.get("clientName")));
        e.setClientPhone(asString(body.get("clientPhone")));
        e.setClientEmail(asString(body.get("clientEmail")));
        e.setDeliveryDate(asString(body.get("deliveryDate")));
        e.setStatus(String.valueOf(body.getOrDefault("status", "sent")));
        e.setNotes(asString(body.get("notes")));
        String kind = asString(body.get("kind"));
        if (kind.isBlank()) {
            kind = "motor";
        }
        e.setKind(kind.trim().toLowerCase());
        e.setRequiresInvoice(asBoolean(body.get("requiresInvoice")));
        Object subtotal = body.get("subtotal");
        Object iva = body.get("iva");
        Object total = body.get("total");
        e.setSubtotal(subtotal == null ? null : new BigDecimal(String.valueOf(subtotal)));
        e.setIva(iva == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(iva)));
        e.setTotal(total == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(total)));
        if (e.getSubtotal() == null) {
            e.setSubtotal(e.getTotal());
        }
        try {
            Object items = body.get("items");
            e.setItemsJson(objectMapper.writeValueAsString(items == null ? List.of() : items));
        } catch (JacksonException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items inválidos");
        }
        return e;
    }

    private Map<String, Object> toMap(PricingQuoteEntity e) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", e.getId());
        map.put("code", e.getCode());
        map.put("source", e.getSource());
        map.put("createdAt", e.getCreatedAt() == null ? Instant.now().toString() : e.getCreatedAt().toString());
        map.put("clientName", e.getClientName());
        map.put("clientPhone", e.getClientPhone());
        map.put("clientEmail", e.getClientEmail());
        map.put("deliveryDate", e.getDeliveryDate());
        map.put("status", e.getStatus());
        map.put("notes", e.getNotes());
        map.put("kind", e.getKind() == null || e.getKind().isBlank() ? "motor" : e.getKind());
        map.put("requiresInvoice", Boolean.TRUE.equals(e.getRequiresInvoice()));
        map.put("orderCode", e.getOrderCode());
        BigDecimal total = e.getTotal() == null ? BigDecimal.ZERO : e.getTotal();
        BigDecimal subtotal = e.getSubtotal() == null ? total : e.getSubtotal();
        BigDecimal iva = e.getIva() == null ? BigDecimal.ZERO : e.getIva();
        map.put("subtotal", subtotal);
        map.put("iva", iva);
        map.put("total", total);
        String json = e.getItemsJson();
        if (json == null || json.isBlank()) {
            map.put("items", List.of());
            return map;
        }
        try {
            map.put("items", objectMapper.readValue(json, List.class));
        } catch (Exception ex) {
            // Si items_json quedó corrupto (p.ej. OID de CLOB), no tumbar el listado
            map.put("items", List.of());
            map.put("itemsParseError", true);
        }
        return map;
    }

    private static boolean asBoolean(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        return "true".equalsIgnoreCase(String.valueOf(value).trim())
                || "1".equals(String.valueOf(value).trim());
    }

    private static String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> publicClientView(Map<String, Object> full) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", full.get("id"));
        map.put("code", full.get("code"));
        map.put("source", full.get("source"));
        map.put("createdAt", full.get("createdAt"));
        map.put("clientName", full.get("clientName"));
        map.put("clientPhone", full.get("clientPhone"));
        map.put("clientEmail", full.get("clientEmail"));
        map.put("deliveryDate", full.get("deliveryDate"));
        map.put("status", full.get("status"));
        map.put("notes", full.get("notes"));
        map.put("kind", full.get("kind"));
        map.put("requiresInvoice", full.get("requiresInvoice"));
        map.put("subtotal", full.get("subtotal"));
        map.put("iva", full.get("iva"));
        Object itemsObj = full.get("items");
        if (itemsObj instanceof List<?> list) {
            map.put("items", list.stream().map(it -> {
                if (!(it instanceof Map<?, ?> raw)) return it;
                Map<String, Object> item = new LinkedHashMap<>();
                raw.forEach((k, v) -> {
                    String key = String.valueOf(k);
                    if ("pricing".equals(key)) return;
                    item.put(key, v);
                });
                return item;
            }).toList());
        } else {
            map.put("items", List.of());
        }
        return map;
    }
}
