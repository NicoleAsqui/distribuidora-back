package ec.distribuidoraguayaquil.domain.model;

import java.math.BigDecimal;

public record ProductVariant(
        String size,
        String color,
        BigDecimal price,
        String tag
) {
}
