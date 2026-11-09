package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.OrderEvent;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import ec.distribuidoraguayaquil.domain.port.in.OrderUseCase;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.AppSecretEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.StaffMemberEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.AppSecretJpaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.StaffMemberJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.Deque;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Calendario de pedidos para el taller: link privado (token) + trabajadores con PIN.
 * La vista pública no muestra precios (solo el saldo por cobrar al entregar).
 */
@Service
public class AgendaService {

    private static final String TOKEN_KEY = "agenda_token";
    private static final Duration DONE_WINDOW = Duration.ofDays(120);
    private static final int MAX_FAILS = 8;
    private static final Duration FAIL_WINDOW = Duration.ofMinutes(10);

    private final OrderUseCase orders;
    private final StaffMemberJpaRepository staff;
    private final AppSecretJpaRepository secrets;
    private final ObjectMapper objectMapper;
    private final String pinSecret;
    private final SecureRandom random = new SecureRandom();
    private final Map<String, Deque<Instant>> fails = new ConcurrentHashMap<>();

    public AgendaService(
            OrderUseCase orders,
            StaffMemberJpaRepository staff,
            AppSecretJpaRepository secrets,
            ObjectMapper objectMapper,
            @Value("${app.admin.token-secret:distribuidora-dev-secret-change-me}") String pinSecret) {
        this.orders = orders;
        this.staff = staff;
        this.secrets = secrets;
        this.objectMapper = objectMapper;
        this.pinSecret = pinSecret;
    }

    // ---------------- DTOs ----------------

    public record StaffDto(Long id, String nombre, boolean activo, Instant createdAt) {
    }

    public record AgendaItemDto(String name, String size, String color, int qty, String image, String seccion) {
    }

    /** Pedido tal como lo ve el taller: sin precios, con el saldo a cobrar. */
    public record AgendaEntryDto(
            String id,
            String code,
            String source,
            String status,
            String clientName,
            String clientPhone,
            Instant createdAt,
            Instant scheduledAt,
            Instant paidAt,
            Instant doneAt,
            String doneBy,
            String fulfillmentMethod,
            String deliveryZone,
            String city,
            String address,
            String mapsUrl,
            String references,
            String notes,
            /** Guía de envío */
            String firstName,
            String lastName,
            String cedula,
            String province,
            Integer packages,
            BigDecimal balance,
            List<AgendaItemDto> items,
            List<OrderEvent> history
    ) {
    }

    // ---------------- Trabajadores ----------------

    public List<StaffDto> listStaff() {
        return staff.findAllByOrderByNombreAsc().stream().map(this::toDto).toList();
    }

    @Transactional
    public StaffDto createStaff(String nombre, String pin) {
        String n = cleanName(nombre);
        if (staff.existsByNombreIgnoreCase(n)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un trabajador con ese nombre.");
        }
        StaffMemberEntity e = new StaffMemberEntity();
        e.setNombre(n);
        e.setPinHash(uniquePinHash(pin, null));
        e.setActivo(true);
        e.setCreatedAt(Instant.now());
        return toDto(staff.save(e));
    }

    @Transactional
    public StaffDto updateStaff(Long id, String nombre, String pin, Boolean activo) {
        StaffMemberEntity e = staff.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trabajador no encontrado"));
        if (nombre != null && !nombre.isBlank()) {
            String n = cleanName(nombre);
            if (!n.equalsIgnoreCase(e.getNombre()) && staff.existsByNombreIgnoreCase(n)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un trabajador con ese nombre.");
            }
            e.setNombre(n);
        }
        if (pin != null && !pin.isBlank()) {
            e.setPinHash(uniquePinHash(pin, e.getId()));
        }
        if (activo != null) {
            e.setActivo(activo);
        }
        return toDto(staff.save(e));
    }

    private String uniquePinHash(String pin, Long selfId) {
        String p = pin == null ? "" : pin.trim();
        if (!p.matches("\\d{4,8}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El PIN debe tener de 4 a 8 números.");
        }
        String h = hashPin(p);
        staff.findByPinHash(h).ifPresent(other -> {
            if (selfId == null || !other.getId().equals(selfId)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese PIN ya lo usa otro trabajador.");
            }
        });
        return h;
    }

    private static String cleanName(String nombre) {
        String n = nombre == null ? "" : nombre.trim().replaceAll("\\s+", " ");
        if (n.isEmpty() || n.length() > 80) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre inválido.");
        }
        return n;
    }

    private StaffDto toDto(StaffMemberEntity e) {
        return new StaffDto(e.getId(), e.getNombre(), e.isActivo(), e.getCreatedAt());
    }

    private String hashPin(String pin) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(("pin:" + pinSecret).getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(pin.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo procesar el PIN", e);
        }
    }

    // ---------------- Link privado ----------------

    @Transactional
    public String currentToken() {
        return secrets.findById(TOKEN_KEY).map(AppSecretEntity::getValor).orElseGet(this::rotateToken);
    }

    @Transactional
    public String rotateToken() {
        byte[] b = new byte[24];
        random.nextBytes(b);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(b);
        AppSecretEntity e = secrets.findById(TOKEN_KEY).orElseGet(AppSecretEntity::new);
        e.setClave(TOKEN_KEY);
        e.setValor(token);
        e.setUpdatedAt(Instant.now());
        secrets.save(e);
        return token;
    }

    private void requireToken(String token) {
        String current = secrets.findById(TOKEN_KEY).map(AppSecretEntity::getValor).orElse(null);
        if (current == null || token == null
                || !MessageDigest.isEqual(current.getBytes(StandardCharsets.UTF_8),
                token.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Calendario no disponible");
        }
    }

    // ---------------- Vista pública ----------------

    public List<AgendaEntryDto> publicEntries(String token) {
        requireToken(token);
        Instant since = Instant.now().minus(DONE_WINDOW);
        return orders.listAll().stream()
                .filter(o -> o.status() == RequestStatus.PAID
                        || ((o.status() == RequestStatus.DONE || o.status() == RequestStatus.DELIVERED)
                        && o.doneAt() != null && o.doneAt().isAfter(since)))
                .map(o -> toEntry(o, false))
                .toList();
    }

    public AgendaEntryDto publicEntry(String token, String id) {
        requireToken(token);
        Order o = orders.getById(id);
        if (o.status() != RequestStatus.PAID && o.status() != RequestStatus.DONE
                && o.status() != RequestStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no disponible");
        }
        return toEntry(o, true);
    }

    /**
     * Marca como hecho. Si queda saldo, el trabajador debe confirmar que lo cobró (collectBalance = true):
     * se registra el pago del saldo y luego se marca hecho. Sin saldo en $0 no se puede.
     */
    @Transactional
    public AgendaEntryDto markDone(String token, String id, String pin, String note, boolean collectBalance,
                                   String clientKey) {
        requireToken(token);
        String who = staffFromPin(pin, clientKey);
        Order o = orders.getById(id);
        if (o.status() != RequestStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se marcan como hechos los pedidos pagados.");
        }
        BigDecimal saldo = o.balance();
        BigDecimal deposit = saldo.signum() > 0 && collectBalance ? saldo : null;
        Order saved = orders.updateStatus(id, RequestStatus.DONE, null, deposit, note, who);
        return toEntry(saved, true);
    }

    /** Abono registrado por un trabajador con su PIN. */
    @Transactional
    public AgendaEntryDto addPayment(String token, String id, String pin, BigDecimal amount, String note,
                                     String clientKey) {
        requireToken(token);
        String who = staffFromPin(pin, clientKey);
        Order o = orders.getById(id);
        if (o.status() != RequestStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se registran abonos en pedidos pagados.");
        }
        return toEntry(orders.addPayment(id, amount, note, who), true);
    }

    @Transactional
    public AgendaEntryDto reschedule(String token, String id, String pin, Instant when, String reason,
                                     String clientKey) {
        requireToken(token);
        String who = staffFromPin(pin, clientKey);
        Order o = orders.getById(id);
        if (o.status() != RequestStatus.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se aplazan pedidos pagados.");
        }
        if (reason == null || reason.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escribe el motivo del aplazamiento.");
        }
        return toEntry(orders.reschedule(id, when, reason, who), true);
    }

    /** Valida el PIN de un trabajador activo; limita intentos fallidos por cliente. */
    private String staffFromPin(String pin, String clientKey) {
        String key = clientKey == null ? "?" : clientKey;
        Deque<Instant> q = fails.computeIfAbsent(key, k -> new ArrayDeque<>());
        Instant now = Instant.now();
        synchronized (q) {
            while (!q.isEmpty() && q.peekFirst().isBefore(now.minus(FAIL_WINDOW))) {
                q.pollFirst();
            }
            if (q.size() >= MAX_FAILS) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "Demasiados intentos. Espera unos minutos.");
            }
        }
        String p = pin == null ? "" : pin.trim();
        var found = p.matches("\\d{4,8}") ? staff.findByPinHashAndActivoTrue(hashPin(p)) : java.util.Optional.<StaffMemberEntity>empty();
        if (found.isEmpty()) {
            synchronized (q) {
                q.addLast(now);
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "PIN incorrecto.");
        }
        synchronized (q) {
            q.clear();
        }
        return found.get().getNombre();
    }

    private AgendaEntryDto toEntry(Order o, boolean withHistory) {
        JsonNode c = null;
        try {
            if (o.checkoutJson() != null && !o.checkoutJson().isBlank()) {
                c = objectMapper.readTree(o.checkoutJson());
            }
        } catch (Exception ignored) {
            c = null;
        }
        List<AgendaItemDto> items = o.items() == null ? List.of() : o.items().stream()
                .map(i -> new AgendaItemDto(i.name(), i.size(), i.color(), i.qty(), i.image(), i.seccion()))
                .toList();
        return new AgendaEntryDto(
                o.id(), o.code(), o.source(), o.status() == null ? null : o.status().name(),
                o.clientName(), o.clientPhone(), o.createdAt(), o.scheduledAt(), o.paidAt(), o.doneAt(),
                o.doneBy(),
                text(c, "fulfillmentMethod"), text(c, "deliveryZone"), text(c, "city"), text(c, "address"),
                text(c, "mapsUrl"), text(c, "references"),
                o.notes() != null && !o.notes().isBlank() ? o.notes() : text(c, "notes"),
                text(c, "firstName"), text(c, "lastName"), text(c, "cedula"), text(c, "province"),
                c != null && c.has("packages") ? c.get("packages").asInt(1) : null,
                o.balance(), items,
                withHistory ? orders.events(o.id()) : List.of());
    }

    private static String text(JsonNode n, String field) {
        if (n == null || !n.isObject()) {
            return null;
        }
        JsonNode v = n.get(field);
        return v == null || v.isNull() ? null : v.asText(null);
    }
}
