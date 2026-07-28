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
        boolean active,
        /** URL pública imagen detalle (~1200px). */
        String image,
        /** URL pública miniatura catálogo (~380px). */
        String imageThumb
) {
}
