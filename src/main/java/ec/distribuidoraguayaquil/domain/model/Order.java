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
        List<OrderItem> items,
        BigDecimal total,
        RequestStatus status,
        Instant createdAt
) {
}
