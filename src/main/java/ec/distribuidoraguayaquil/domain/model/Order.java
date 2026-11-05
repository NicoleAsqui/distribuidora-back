package ec.distribuidoraguayaquil.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record Order(
        String id,
        String code,
        String clientName,
        String clientEmail,
        String clientPhone,
        String notes,
        String checkoutJson,
        List<OrderItem> items,
        BigDecimal total,
        RequestStatus status,
        Instant createdAt,
        /** web | manual */
        String source,
        /** Fecha y hora pactada de entrega (al pasar a Pagado). */
        Instant scheduledAt,
        Instant paidAt,
        Instant doneAt,
        String doneBy,
        /** Suma de abonos recibidos. */
        BigDecimal paidAmount
) {

    /** Constructor corto (pedidos nuevos sin datos de agenda). */
    public Order(String id, String code, String clientName, String clientEmail, String clientPhone,
                 String notes, String checkoutJson, List<OrderItem> items, BigDecimal total,
                 RequestStatus status, Instant createdAt) {
        this(id, code, clientName, clientEmail, clientPhone, notes, checkoutJson, items, total, status,
                createdAt, "web", null, null, null, null, BigDecimal.ZERO);
    }

    /** Saldo pendiente = total − abonos (nunca negativo). */
    public BigDecimal balance() {
        BigDecimal t = total == null ? BigDecimal.ZERO : total;
        BigDecimal p = paidAmount == null ? BigDecimal.ZERO : paidAmount;
        BigDecimal b = t.subtract(p);
        return b.signum() < 0 ? BigDecimal.ZERO : b;
    }

    public Order withCheckoutJson(String json) {
        return new Order(id, code, clientName, clientEmail, clientPhone, notes, json, items, total, status,
                createdAt, source, scheduledAt, paidAt, doneAt, doneBy, paidAmount);
    }

    public Order withStatus(RequestStatus s) {
        return new Order(id, code, clientName, clientEmail, clientPhone, notes, checkoutJson, items, total, s,
                createdAt, source, scheduledAt, paidAt, doneAt, doneBy, paidAmount);
    }

    public Order withAgenda(RequestStatus s, Instant scheduled, Instant paid, Instant done, String by,
                            BigDecimal paidAmt) {
        return new Order(id, code, clientName, clientEmail, clientPhone, notes, checkoutJson, items, total, s,
                createdAt, source, scheduled, paid, done, by, paidAmt);
    }
}
