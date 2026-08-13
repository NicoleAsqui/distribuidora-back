package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

import java.util.Map;

/** Conteos de productos activos para filtros del storefront. */
public record CatalogCountsDto(
        long total,
        Map<String, Long> byDesign,
        Map<String, Long> byIdea
) {
}
