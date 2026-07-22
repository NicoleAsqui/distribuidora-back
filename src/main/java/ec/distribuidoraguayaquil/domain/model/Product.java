package ec.distribuidoraguayaquil.domain.model;

import java.util.List;

public record Product(
        String id,
        String ref,
        String name,
        String category,
        String shortDesc,
        boolean top,
        List<ProductVariant> variants,
        boolean active
) {
}
