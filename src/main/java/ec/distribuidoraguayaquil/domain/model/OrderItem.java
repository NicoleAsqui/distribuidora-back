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
        String image,
        /** Sección del catálogo: cartulina, acetato, mdf, carton, tarjetas, varios (opcional). */
        String seccion
) {
    /** Sin sección (pedidos antiguos). */
    public OrderItem(String productRef, String name, String size, String color, BigDecimal price, int qty,
                     String image) {
        this(productRef, name, size, color, price, qty, image, null);
    }
}
