package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

import java.util.List;

/** Respuesta paginada de tarjetas de producto del storefront. */
public record ProductPageDto(
        List<ProductCardDto> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static ProductPageDto of(List<ProductCardDto> content, int page, int size, long totalElements) {
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / (double) size);
        if (totalElements > 0 && totalPages == 0) {
            totalPages = 1;
        }
        return new ProductPageDto(content, page, size, totalElements, totalPages);
    }

    public static ProductPageDto empty(int page, int size) {
        return new ProductPageDto(List.of(), page, size, 0, 0);
    }
}
