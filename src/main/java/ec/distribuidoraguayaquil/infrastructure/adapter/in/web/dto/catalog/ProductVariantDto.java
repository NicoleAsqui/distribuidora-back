package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

import java.math.BigDecimal;

/** Un escalón de precio: {@code tag} es la cantidad mínima desde la que aplica. */
public record ProductVariantDto(
        String size,
        String color,
        BigDecimal price,
        String tag
) {
}
