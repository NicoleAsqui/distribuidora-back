package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.OrderEvent;
import ec.distribuidoraguayaquil.domain.model.OrderItem;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import ec.distribuidoraguayaquil.domain.port.in.OrderUseCase;
import ec.distribuidoraguayaquil.domain.port.out.OrderRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.OrderEventEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.OrderEventJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class OrderService implements OrderUseCase {

    /** Ecuador continental (UTC−5, sin horario de verano). */
    public static final ZoneId ECUADOR = ZoneId.of("America/Guayaquil");
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("EEE d MMM yyyy, HH:mm", Locale.forLanguageTag("es-EC")).withZone(ECUADOR);

    private static final BigDecimal PICKUP_DISCOUNT_RATE = new BigDecimal("0.03");
    private static final BigDecimal IVA_RATE = new BigDecimal("0.15");
    private static final BigDecimal CARD_SURCHARGE_RATE = new BigDecimal("0.06");

    private final OrderRepositoryPort repository;
    private final OrderEventJpaRepository events;
    private final ObjectMapper objectMapper;

    private final ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteRepository variantes;
    private final ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.DisenoRepository disenos;

    @org.springframework.beans.factory.annotation.Autowired
    public OrderService(OrderRepositoryPort repository, OrderEventJpaRepository events, ObjectMapper objectMapper,
                        ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteRepository variantes,
                        ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.DisenoRepository disenos) {
        this.repository = repository;
        this.events = events;
        this.objectMapper = objectMapper;
        this.variantes = variantes;
        this.disenos = disenos;
    }

    /** Sin catálogo (pruebas): la sección no se completa. */
    public OrderService(OrderRepositoryPort repository, OrderEventJpaRepository events, ObjectMapper objectMapper) {
        this(repository, events, objectMapper, null, null);
    }

    /**
     * Completa la sección de cada producto que no la tenga (pedidos antiguos), buscándola en el catálogo:
     * REF (SKU) → variante → diseño → sección. No modifica la base de datos.
     */
    private Order withSecciones(Order o, java.util.Map<String, String> cache) {
        if (variantes == null || disenos == null || o.items() == null
                || o.items().stream().allMatch(i -> i.seccion() != null && !i.seccion().isBlank())) {
            return o;
        }
        List<OrderItem> items = o.items().stream().map(i -> {
            if (i.seccion() != null && !i.seccion().isBlank()) {
                return i;
            }
            String sec = seccionForRef(i.productRef(), cache);
            return sec == null ? i
                    : new OrderItem(i.productRef(), i.name(), i.size(), i.color(), i.price(), i.qty(), i.image(), sec);
        }).toList();
        return new Order(o.id(), o.code(), o.clientName(), o.clientEmail(), o.clientPhone(), o.notes(),
                o.checkoutJson(), items, o.total(), o.status(), o.createdAt(), o.source(), o.scheduledAt(),
                o.paidAt(), o.doneAt(), o.doneBy(), o.paidAmount());
    }

    private String seccionForRef(String ref, java.util.Map<String, String> cache) {
        if (ref == null || ref.isBlank()) {
            return null;
        }
        if (cache.containsKey(ref)) {
            return cache.get(ref);
        }
        String sec = null;
        try {
            sec = variantes.findBySku(ref.trim())
                    .flatMap(v -> disenos.findById(v.getDisenoId()))
                    .map(d -> d.getSeccion())
                    .filter(x -> x != null && !x.isBlank())
                    .orElse(null);
        } catch (Exception ignored) {
            sec = null;
        }
        cache.put(ref, sec);
        return sec;
    }

    /** Un pedido pendiente vale 1 semana: si no se paga, caduca (igual que las cotizaciones). */
    public static final java.time.Duration VALIDITY = java.time.Duration.ofDays(7);

    public static Instant validUntil(Order o) {
        return (o.createdAt() == null ? Instant.now() : o.createdAt()).plus(VALIDITY);
    }

    /** Pendiente vencido → Caducado (se guarda y queda en el historial). */
    private Order expireIfNeeded(Order o) {
        if (o.status() == RequestStatus.PENDING && Instant.now().isAfter(validUntil(o))) {
            Order saved = repository.save(o.withStatus(RequestStatus.EXPIRED));
            log(o.id(), "Sistema", "STATUS",
                    "Estado: Pendiente → Caducado · no se registró el pago antes del " + FMT.format(validUntil(o)), null);
            return saved;
        }
        return o;
    }

    @Override
    @Transactional
    public List<Order> listAll() {
        java.util.Map<String, String> cache = new java.util.HashMap<>();
        return repository.findAll().stream().map(this::expireIfNeeded).map(o -> withSecciones(o, cache)).toList();
    }

    @Override
    @Transactional
    public Order getById(String id) {
        return withSecciones(expireIfNeeded(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"))),
                new java.util.HashMap<>());
    }

    @Override
    @Transactional
    public Order getByCode(String code) {
        return withSecciones(expireIfNeeded(repository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"))),
                new java.util.HashMap<>());
    }

    @Override
    @Transactional
    public Order create(Order order) {
        Order saved = repository.save(newOrder(order, "web"));
        log(saved.id(), "Cliente (web)", "CREATED", "Pedido recibido desde la web", null);
        return saved;
    }

    @Override
    @Transactional
    public Order createManual(Order order, String actor) {
        Order saved = repository.save(newOrder(order, "manual"));
        log(saved.id(), actor, "CREATED", "Pedido ingresado manualmente", null);
        return saved;
    }

    @Override
    @Transactional
    public Order createFromQuote(Order order, BigDecimal quoteTotal, String actor, String quoteCode) {
        Order base = newOrder(order, "cotizacion");
        BigDecimal total = quoteTotal == null || quoteTotal.signum() <= 0
                ? base.total()
                : quoteTotal.setScale(2, RoundingMode.HALF_UP);
        Order saved = repository.save(new Order(base.id(), base.code(), base.clientName(), base.clientEmail(),
                base.clientPhone(), base.notes(), base.checkoutJson(), base.items(), total, base.status(),
                base.createdAt(), base.source(), null, null, null, null, BigDecimal.ZERO));
        log(saved.id(), actor, "CREATED", "Pedido creado desde la cotización " + quoteCode + " (aceptada por el cliente)",
                null);
        return saved;
    }

    private Order newOrder(Order order, String source) {
        java.util.Map<String, String> cache = new java.util.HashMap<>();
        List<OrderItem> items = (order.items() == null ? List.<OrderItem>of() : order.items()).stream()
                .map(i -> {
                    if (i.seccion() != null && !i.seccion().isBlank()) return i;
                    String sec = variantes == null ? null : seccionForRef(i.productRef(), cache);
                    return sec == null ? i
                            : new OrderItem(i.productRef(), i.name(), i.size(), i.color(), i.price(), i.qty(), i.image(), sec);
                })
                .toList();
        BigDecimal total = computeTotal(items, order.checkoutJson());
        long seq = repository.nextSequence();
        return new Order(
                UUID.randomUUID().toString(),
                "PED-" + seq,
                order.clientName(),
                order.clientEmail(),
                order.clientPhone(),
                order.notes(),
                order.checkoutJson(),
                items,
                total,
                RequestStatus.PENDING,
                Instant.now(),
                source,
                null, null, null, null,
                BigDecimal.ZERO
        );
    }

    /**
     * Total igual al del checkout web: subtotal − 3% retiro, + IVA 15% si pide factura,
     * + 6% si paga con tarjeta o PayPal.
     */
    BigDecimal computeTotal(List<OrderItem> items, String checkoutJson) {
        BigDecimal subtotal = items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.qty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        JsonNode c = readJson(checkoutJson);
        BigDecimal after = subtotal;
        if (c != null && "pickup".equalsIgnoreCase(text(c, "fulfillmentMethod")) && subtotal.signum() > 0) {
            after = subtotal.subtract(subtotal.multiply(PICKUP_DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP));
        }
        BigDecimal iva = BigDecimal.ZERO;
        if (c != null && c.has("requiresInvoice") && c.get("requiresInvoice").asBoolean(false)) {
            iva = after.multiply(IVA_RATE).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal before = after.add(iva).setScale(2, RoundingMode.HALF_UP);
        String pay = c == null ? "" : text(c, "paymentMethod");
        BigDecimal surcharge = ("tarjeta".equalsIgnoreCase(pay) || "paypal".equalsIgnoreCase(pay))
                ? before.multiply(CARD_SURCHARGE_RATE).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        return before.add(surcharge).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    @Transactional
    public Order updateStatus(String id, RequestStatus status, Instant scheduledAt, BigDecimal deposit, String note,
                              String actor) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado requerido");
        }
        Order o = getById(id);
        Instant now = Instant.now();
        Instant scheduled = o.scheduledAt();
        Instant paidAt = o.paidAt();
        Instant doneAt = o.doneAt();
        String doneBy = o.doneBy();
        BigDecimal paid = nz(o.paidAmount());
        BigDecimal total = nz(o.total());
        BigDecimal dep = deposit != null && deposit.signum() > 0 ? deposit.setScale(2, RoundingMode.HALF_UP) : null;
        if (dep != null && status != RequestStatus.PENDING && status != RequestStatus.CANCELED) {
            if (paid.add(dep).compareTo(total) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El abono supera el saldo pendiente ($" + total.subtract(paid).setScale(2, RoundingMode.HALF_UP) + ").");
            }
            paid = paid.add(dep).setScale(2, RoundingMode.HALF_UP);
        } else {
            dep = null;
        }
        StringBuilder msg = new StringBuilder("Estado: ")
                .append(label(o.status())).append(" → ").append(label(status));

        if (o.status() == RequestStatus.EXPIRED) {
            if (status == RequestStatus.PENDING) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Un pedido caducado no vuelve a Pendiente: regístralo como Pagado (con nota) o cancélalo.");
            }
            if (status == RequestStatus.PAID) {
                if (note == null || note.isBlank()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "El pedido está caducado: escribe una nota explicando por qué se acepta el pago.");
                }
                msg.append(" · pagado fuera de plazo (caducado)");
            }
        }

        switch (status) {
            case PAID -> {
                Instant when = scheduledAt != null ? scheduledAt : scheduled;
                if (when == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Para pasar a Pagado indica la fecha y hora de entrega.");
                }
                if (paid.signum() <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Para pasar a Pagado registra el pago: el abono o el valor completo.");
                }
                if (scheduledAt != null && scheduled != null && !scheduledAt.equals(scheduled)) {
                    msg.append(" · fecha cambiada de ").append(FMT.format(scheduled)).append(" a ")
                            .append(FMT.format(scheduledAt));
                } else {
                    msg.append(" · agendado para ").append(FMT.format(when));
                }
                scheduled = when;
                if (paidAt == null) {
                    paidAt = now;
                }
                // Reabrir un pedido hecho lo devuelve al calendario.
                doneAt = null;
                doneBy = null;
            }
            case DONE, DELIVERED -> {
                BigDecimal saldo = total.subtract(paid);
                if (saldo.signum() > 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "No se puede marcar como hecho: queda un saldo de $" + saldo.setScale(2, RoundingMode.HALF_UP)
                                    + ". Registra el pago primero.");
                }
                doneAt = now;
                doneBy = actor;
            }
            case PENDING -> {
                // Volver a Pendiente (solo admin): anula abonos y fecha; sirve para pruebas o correcciones.
                if (paid.signum() > 0) {
                    msg.append(" · abonos anulados ($").append(paid.setScale(2, RoundingMode.HALF_UP)).append(")");
                }
                if (scheduled != null) {
                    msg.append(" · se quitó la fecha agendada");
                }
                paid = BigDecimal.ZERO;
                paidAt = null;
                scheduled = null;
                doneAt = null;
                doneBy = null;
            }
            default -> {
                if (status == RequestStatus.CANCELED) {
                    doneAt = null;
                    doneBy = null;
                }
            }
        }
        if (note != null && !note.isBlank()) {
            msg.append(" · ").append(note.trim());
        }

        Order saved = repository.save(o.withAgenda(status, scheduled, paidAt, doneAt, doneBy, paid));
        if (dep != null) {
            log(id, actor, "PAYMENT", paymentMessage(saved, null), dep);
        }
        log(id, actor, "STATUS", msg.toString(), null);
        return saved;
    }

    @Override
    @Transactional
    public Order reschedule(String id, Instant scheduledAt, String reason, String actor) {
        if (scheduledAt == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indica la nueva fecha y hora.");
        }
        Order o = getById(id);
        if (o.status() == RequestStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El pedido está cancelado.");
        }
        String from = o.scheduledAt() == null ? "sin fecha" : FMT.format(o.scheduledAt());
        Order saved = repository.save(o.withAgenda(o.status(), scheduledAt, o.paidAt(), o.doneAt(), o.doneBy(),
                nz(o.paidAmount())));
        String msg = "Aplazado: " + from + " → " + FMT.format(scheduledAt)
                + (reason == null || reason.isBlank() ? "" : " · Motivo: " + reason.trim());
        log(id, actor, "SCHEDULE", msg, null);
        return saved;
    }

    @Override
    @Transactional
    public Order addPayment(String id, BigDecimal amount, String note, String actor) {
        if (amount == null || amount.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El abono debe ser mayor a 0.");
        }
        Order o = getById(id);
        if (o.status() == RequestStatus.CANCELED || o.status() == RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Primero pasa el pedido a Pagado para registrar abonos.");
        }
        BigDecimal saldo = o.balance();
        if (amount.setScale(2, RoundingMode.HALF_UP).compareTo(saldo) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El abono supera el saldo pendiente ($" + saldo.setScale(2, RoundingMode.HALF_UP) + ").");
        }
        BigDecimal paid = nz(o.paidAmount()).add(amount).setScale(2, RoundingMode.HALF_UP);
        Order saved = repository.save(o.withAgenda(o.status(), o.scheduledAt(), o.paidAt(), o.doneAt(), o.doneBy(),
                paid));
        log(id, actor, "PAYMENT", paymentMessage(saved, note), amount.setScale(2, RoundingMode.HALF_UP));
        return saved;
    }

    @Override
    @Transactional
    public OrderEvent addEvent(String id, String type, String message, String actor) {
        getById(id);
        String t = type == null ? "NOTE" : type.trim().toUpperCase(Locale.ROOT);
        if (!t.equals("NOTE") && !t.equals("REMINDER")) {
            t = "NOTE";
        }
        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escribe la nota.");
        }
        return toDomain(log(id, actor, t, message.trim(), null));
    }

    @Override
    public List<OrderEvent> events(String id) {
        return events.findByOrderIdOrderByCreatedAtAscIdAsc(id).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public Order attachPdfUrl(String code, String pdfUrl) {
        if (pdfUrl == null || pdfUrl.isBlank() || !pdfUrl.startsWith("https://")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pdfUrl inválida");
        }
        Order existing = getByCode(code);
        String merged = mergePdfUrl(existing.checkoutJson(), pdfUrl.trim());
        return repository.save(existing.withCheckoutJson(merged));
    }

    // ---------------- helpers ----------------

    private static String paymentMessage(Order saved, String note) {
        BigDecimal saldo = saved.balance().setScale(2, RoundingMode.HALF_UP);
        String head = saldo.signum() == 0 ? "Pago completo · saldo $0.00" : "Abono registrado · queda saldo $" + saldo;
        return head + (note == null || note.isBlank() ? "" : " · " + note.trim());
    }

    private OrderEventEntity log(String orderId, String actor, String type, String message, BigDecimal amount) {
        OrderEventEntity e = new OrderEventEntity();
        e.setOrderId(orderId);
        e.setCreatedAt(Instant.now());
        e.setActor(actor == null || actor.isBlank() ? "Sistema" : actor);
        e.setType(type);
        e.setMessage(message);
        e.setAmount(amount);
        return events.save(e);
    }

    private OrderEvent toDomain(OrderEventEntity e) {
        return new OrderEvent(e.getId(), e.getOrderId(), e.getCreatedAt(), e.getActor(), e.getType(),
                e.getMessage(), e.getAmount());
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    static String label(RequestStatus s) {
        if (s == null) {
            return "—";
        }
        return switch (s) {
            case PENDING -> "Pendiente";
            case SENT -> "Enviada";
            case PRODUCTION -> "Producción";
            case DELIVERED -> "Entregada";
            case PAID -> "Pagado";
            case DONE -> "Hecho";
            case EXPIRED -> "Caducado";
            case CANCELED -> "Cancelado";
        };
    }

    private JsonNode readJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            JsonNode n = objectMapper.readTree(json);
            return n != null && n.isObject() ? n : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String text(JsonNode n, String field) {
        JsonNode v = n.get(field);
        return v == null || v.isNull() ? "" : v.asText("");
    }

    @Override
    @Transactional
    public Order updateShipping(String id, String firstName, String lastName, String cedula, Integer packages,
                                String province, String city, String note, String actor) {
        Order o = getById(id);
        ObjectNode node;
        JsonNode tree = readJson(o.checkoutJson());
        node = tree == null ? objectMapper.createObjectNode() : (ObjectNode) tree;
        List<String> changes = new java.util.ArrayList<>();
        if (cedula != null && !cedula.isBlank()) {
            String c = cedula.replaceAll("\\D", "");
            if (!c.matches("\\d{10}|\\d{13}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cédula debe tener 10 dígitos (o RUC de 13).");
            }
            if (!c.equals(text(node, "cedula"))) changes.add("cédula " + c);
            node.put("cedula", c);
        }
        if (packages != null) {
            if (packages < 1 || packages > 999) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número de paquetes inválido.");
            }
            if (!String.valueOf(packages).equals(text(node, "packages"))) changes.add(packages + " paquete" + (packages == 1 ? "" : "s"));
            node.put("packages", packages);
        }
        if (firstName != null && !firstName.isBlank()) node.put("firstName", firstName.trim());
        if (lastName != null && !lastName.isBlank()) node.put("lastName", lastName.trim());
        if ((firstName != null && !firstName.isBlank()) || (lastName != null && !lastName.isBlank())) {
            String full = (text(node, "firstName") + " " + text(node, "lastName")).trim();
            node.put("fullName", full);
            changes.add("recibe " + full);
        }
        if (province != null && !province.isBlank()) {
            if (!province.trim().equals(text(node, "province"))) changes.add("provincia " + province.trim());
            node.put("province", province.trim());
        }
        if (city != null && !city.isBlank()) {
            if (!city.trim().equals(text(node, "city"))) changes.add("ciudad " + city.trim());
            node.put("city", city.trim());
        }
        String json;
        try {
            json = objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos inválidos");
        }
        String fullName = text(node, "fullName");
        Order base = o.withCheckoutJson(json);
        Order saved = repository.save(fullName.isBlank() ? base : new Order(base.id(), base.code(), fullName,
                base.clientEmail(), base.clientPhone(), base.notes(), base.checkoutJson(), base.items(), base.total(),
                base.status(), base.createdAt(), base.source(), base.scheduledAt(), base.paidAt(), base.doneAt(),
                base.doneBy(), base.paidAmount()));
        if (!changes.isEmpty()) {
            log(id, actor, "NOTE", "Datos de guía: " + String.join(", ", changes)
                    + (note == null || note.isBlank() ? "" : " · " + note.trim()), null);
        }
        return saved;
    }

    @Override
    @Transactional
    public Order updateContact(String id, String phone, String email, String actor) {
        Order o = getById(id);
        JsonNode tree = readJson(o.checkoutJson());
        ObjectNode node = tree == null ? objectMapper.createObjectNode() : (ObjectNode) tree;
        List<String> changes = new java.util.ArrayList<>();
        String newPhone = o.clientPhone();
        String newEmail = o.clientEmail();
        if (phone != null && !phone.isBlank() && !phone.trim().equals(o.clientPhone())) {
            newPhone = phone.trim();
            node.put("phone", newPhone);
            changes.add("teléfono " + newPhone);
        }
        if (email != null && !email.trim().equals(o.clientEmail() == null ? "" : o.clientEmail())) {
            newEmail = email.trim();
            node.put("email", newEmail);
            changes.add(newEmail.isEmpty() ? "email borrado" : "email " + newEmail);
        }
        if (changes.isEmpty()) {
            return o;
        }
        String json;
        try {
            json = objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos inválidos");
        }
        Order saved = repository.save(new Order(o.id(), o.code(), o.clientName(), newEmail, newPhone, o.notes(), json,
                o.items(), o.total(), o.status(), o.createdAt(), o.source(), o.scheduledAt(), o.paidAt(), o.doneAt(),
                o.doneBy(), o.paidAmount()));
        log(id, actor, "NOTE", "Datos del cliente: " + String.join(", ", changes), null);
        return saved;
    }

    /** Cédula guardada en el pedido (para exigirla al pasar a Pagado). */
    public boolean hasCedula(Order o) {
        JsonNode n = readJson(o.checkoutJson());
        return n != null && !text(n, "cedula").isBlank();
    }

    @Override
    @Transactional
    public Order updateDelivery(String id, String fulfillmentMethod, String city, String address, String references,
                                String mapsUrl, String note, String actor) {
        String method = "delivery".equalsIgnoreCase(fulfillmentMethod) ? "delivery" : "pickup";
        Order o = getById(id);
        if (o.status() == RequestStatus.CANCELED || o.status() == RequestStatus.DONE
                || o.status() == RequestStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede cambiar la entrega de un pedido " + label(o.status()).toLowerCase(Locale.ROOT) + ".");
        }
        String c = city == null || city.isBlank() ? "Guayaquil" : city.trim();
        if ("delivery".equals(method) && (address == null || address.isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escribe la dirección de envío.");
        }
        ObjectNode node;
        try {
            JsonNode tree = readJson(o.checkoutJson());
            node = tree == null ? objectMapper.createObjectNode() : (ObjectNode) tree;
        } catch (Exception e) {
            node = objectMapper.createObjectNode();
        }
        String before = "delivery".equals(text(node, "fulfillmentMethod")) ? "Envío" : "Retiro en tienda";
        String beforeAddr = text(node, "address");
        node.put("fulfillmentMethod", method);
        if ("delivery".equals(method)) {
            node.put("deliveryZone", "Guayaquil".equalsIgnoreCase(c) ? "guayaquil" : "fuera");
            node.put("city", c);
            node.put("address", address.trim());
            node.put("references", references == null ? "" : references.trim());
            node.put("mapsUrl", mapsUrl == null ? "" : mapsUrl.trim());
        } else {
            node.put("deliveryZone", "guayaquil");
            node.put("city", "Guayaquil");
            node.put("address", "");
            node.put("references", "");
            node.put("mapsUrl", "");
            node.putNull("lat");
            node.putNull("lng");
        }
        String json;
        try {
            json = objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de entrega inválidos");
        }
        // Pedidos de cotización conservan el total pactado; el resto se recalcula (descuento de retiro).
        BigDecimal newTotal = "cotizacion".equals(o.source()) ? nz(o.total()) : computeTotal(o.items(), json);
        Order saved = repository.save(o.withCheckoutAndTotal(json, newTotal));

        String after = "delivery".equals(method) ? "Envío" : "Retiro en tienda";
        StringBuilder msg = new StringBuilder("Entrega cambiada: ").append(before).append(" → ").append(after);
        if ("delivery".equals(method)) {
            msg.append(" (").append(address.trim()).append(", ").append(c).append(")");
        } else if (!beforeAddr.isBlank()) {
            msg.append(" (antes: ").append(beforeAddr).append(")");
        }
        if (nz(o.total()).compareTo(newTotal) != 0) {
            msg.append(" · total $").append(nz(o.total()).setScale(2, RoundingMode.HALF_UP))
                    .append(" → $").append(newTotal.setScale(2, RoundingMode.HALF_UP));
        }
        if (note != null && !note.isBlank()) {
            msg.append(" · ").append(note.trim());
        }
        log(id, actor, "NOTE", msg.toString(), null);
        return saved;
    }

    private String mergePdfUrl(String checkoutJson, String pdfUrl) {
        try {
            ObjectNode node;
            if (checkoutJson == null || checkoutJson.isBlank()) {
                node = objectMapper.createObjectNode();
            } else {
                var tree = objectMapper.readTree(checkoutJson);
                node = tree.isObject() ? (ObjectNode) tree : objectMapper.createObjectNode();
            }
            node.put("pdfUrl", pdfUrl);
            return objectMapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "checkoutJson inválido");
        }
    }
}
