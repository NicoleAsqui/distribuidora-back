package ec.distribuidoraguayaquil.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/** Entrada del historial de un pedido. */
public record OrderEvent(
        Long id,
        String orderId,
        Instant createdAt,
        String actor,
        /** CREATED | STATUS | SCHEDULE | PAYMENT | NOTE | REMINDER */
        String type,
        String message,
        BigDecimal amount
) {
}
