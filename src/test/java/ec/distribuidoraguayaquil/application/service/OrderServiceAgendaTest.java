package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.OrderItem;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import ec.distribuidoraguayaquil.domain.port.out.OrderRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.OrderEventEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.OrderEventJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Lógica de agenda sin base de datos (repositorios en memoria). */
class OrderServiceAgendaTest {

    private final Map<String, Order> store = new HashMap<>();
    private final List<OrderEventEntity> log = new ArrayList<>();
    private OrderService service;

    @BeforeEach
    void setUp() {
        OrderRepositoryPort repo = new OrderRepositoryPort() {
            public List<Order> findAll() { return new ArrayList<>(store.values()); }
            public Optional<Order> findById(String id) { return Optional.ofNullable(store.get(id)); }
            public Optional<Order> findByCode(String code) {
                return store.values().stream().filter(o -> o.code().equals(code)).findFirst();
            }
            public Order save(Order o) { store.put(o.id(), o); return o; }
            public long nextSequence() { return 1001 + store.size(); }
        };
        OrderEventJpaRepository events = mock(OrderEventJpaRepository.class);
        when(events.save(any(OrderEventEntity.class))).thenAnswer(inv -> {
            OrderEventEntity e = inv.getArgument(0);
            e.setId((long) log.size() + 1);
            log.add(e);
            return e;
        });
        when(events.findByOrderIdOrderByCreatedAtAscIdAsc(anyString())).thenAnswer(inv ->
                log.stream().filter(e -> e.getOrderId().equals(inv.getArgument(0))).toList());
        service = new OrderService(repo, events, new ObjectMapper());
    }

    private Order newOrder(String checkoutJson) {
        return new Order(null, null, "Ana", null, "+593 099", "", checkoutJson,
                List.of(new OrderItem("REF", "Caja", "10x10", "Kraft", new BigDecimal("2.00"), 50, null)),
                null, null, null);
    }

    @Test
    void totalIncluyeIvaYRecargoTarjeta() {
        Order o = service.create(newOrder("{\"requiresInvoice\":true,\"paymentMethod\":\"tarjeta\"}"));
        // 100 + 15% IVA = 115 ; +6% = 121.90
        assertEquals(new BigDecimal("121.90"), o.total());
        assertEquals("web", o.source());
        assertEquals(RequestStatus.PENDING, o.status());
    }

    @Test
    void retiroDescuentaTresPorCiento() {
        Order o = service.create(newOrder("{\"fulfillmentMethod\":\"pickup\",\"paymentMethod\":\"transferencia\"}"));
        assertEquals(new BigDecimal("97.00"), o.total());
    }

    @Test
    void pagadoExigeFechaYRegistraAbonoEHistorial() {
        Order o = service.createManual(newOrder(null), "Jeymy");
        assertEquals("manual", o.source());
        assertThrows(ResponseStatusException.class,
                () -> service.updateStatus(o.id(), RequestStatus.PAID, null, null, null, "Jeymy"));

        Instant when = Instant.parse("2026-11-12T17:00:00Z");
        Order paid = service.updateStatus(o.id(), RequestStatus.PAID, when, new BigDecimal("40"), null, "Jeymy");
        assertEquals(RequestStatus.PAID, paid.status());
        assertEquals(when, paid.scheduledAt());
        assertNotNull(paid.paidAt());
        assertEquals(new BigDecimal("40.00"), paid.paidAmount());
        assertEquals(new BigDecimal("60.00"), paid.balance());

        Order later = service.reschedule(o.id(), Instant.parse("2026-11-14T17:00:00Z"), "faltó material", "Miguel");
        assertEquals(Instant.parse("2026-11-14T17:00:00Z"), later.scheduledAt());

        // Con saldo pendiente no se puede marcar hecho…
        assertThrows(ResponseStatusException.class,
                () -> service.updateStatus(o.id(), RequestStatus.DONE, null, null, null, "Sarita"));
        // …pero sí si cobra el saldo en el mismo paso.
        Order done = service.updateStatus(o.id(), RequestStatus.DONE, null, new BigDecimal("60"), null, "Sarita");
        assertEquals("Sarita", done.doneBy());
        assertEquals(0, done.balance().signum());
        assertNotNull(done.doneAt());

        // Reabrir lo devuelve al calendario (sin marca de hecho).
        Order reopened = service.updateStatus(o.id(), RequestStatus.PAID, null, null, null, "Jeymy");
        assertNull(reopened.doneAt());

        var history = service.events(o.id());
        assertTrue(history.stream().anyMatch(e -> e.type().equals("PAYMENT") && e.actor().equals("Jeymy")));
        assertTrue(history.stream().anyMatch(e -> e.type().equals("SCHEDULE") && e.message().contains("faltó material")
                && e.actor().equals("Miguel")));
        assertTrue(history.stream().anyMatch(e -> e.type().equals("PAYMENT") && e.message().startsWith("Pago completo")
                && e.actor().equals("Sarita")));
    }

    @Test
    void volverAPendienteAnulaAbonosYFecha() {
        Order o = service.createManual(newOrder(null), "Jeymy");
        service.updateStatus(o.id(), RequestStatus.PAID, Instant.parse("2026-11-12T17:00:00Z"), new BigDecimal("30"), null, "Jeymy");
        Order back = service.updateStatus(o.id(), RequestStatus.PENDING, null, null, "prueba", "Jeymy");
        assertEquals(0, back.paidAmount().signum());
        assertNull(back.scheduledAt());
        assertTrue(service.events(o.id()).stream().anyMatch(e -> e.message().contains("abonos anulados")));
    }

    @Test
    void abonoNoSuperaSaldo() {
        Order o = service.createManual(newOrder(null), "Jeymy");
        service.updateStatus(o.id(), RequestStatus.PAID, Instant.parse("2026-11-12T17:00:00Z"), null, null, "Jeymy");
        assertThrows(ResponseStatusException.class, () -> service.addPayment(o.id(), new BigDecimal("150"), null, "x"));
        Order p1 = service.addPayment(o.id(), new BigDecimal("30"), null, "Miguel");
        assertEquals(new BigDecimal("70.00"), p1.balance());
        Order p2 = service.addPayment(o.id(), new BigDecimal("70"), null, "Miguel");
        assertEquals(0, p2.balance().signum());
    }

    @Test
    void abonoCompletaSaldo() {
        Order o = service.createManual(newOrder(null), "Jeymy");
        service.updateStatus(o.id(), RequestStatus.PAID, Instant.parse("2026-11-12T17:00:00Z"), null, null, "Jeymy");
        Order p = service.addPayment(o.id(), new BigDecimal("100"), "transferencia", "Jeymy");
        assertEquals(0, p.balance().signum());
        assertThrows(ResponseStatusException.class, () -> service.addPayment(o.id(), BigDecimal.ZERO, null, "x"));
    }
}
