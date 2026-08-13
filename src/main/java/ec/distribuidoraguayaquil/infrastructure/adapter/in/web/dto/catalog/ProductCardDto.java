package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

import java.util.List;

/** Tarjeta de producto para el storefront, construida a partir de una variante. */
public record ProductCardDto(
        String id,
        String ref,
        String name,
        String category,
        Long designId,
        String designSlug,
        String shortDesc,
        boolean top,
        boolean active,
        String image,
        String imageThumb,
        List<ProductVariantDto> variants
) {
}
