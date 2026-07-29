package ec.distribuidoraguayaquil.domain.model;

import java.math.BigDecimal;

public record OrderItem(
        String productRef,
        String name,
        String size,
        String color,
        BigDecimal price,
        int qty,
        /** URL imagen del producto al momento del pedido (opcional). */
        String image
) {
}
