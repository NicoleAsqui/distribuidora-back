package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.QuoteAdminMailService;
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

    public PricingQuoteController(
            PricingQuoteJpaRepository repository,
            ObjectMapper objectMapper,
            QuoteAdminMailService quoteAdminMailService) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.quoteAdminMailService = quoteAdminMailService;
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
