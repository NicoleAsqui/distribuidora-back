package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

import java.util.List;

/** Respuesta paginada genérica para listados admin. */
public record PageDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static <T> PageDto<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / (double) size);
        if (totalElements > 0 && totalPages == 0) {
            totalPages = 1;
        }
        return new PageDto<>(content, page, size, totalElements, totalPages);
    }

    public static <T> PageDto<T> empty(int page, int size) {
        return new PageDto<>(List.of(), page, size, 0, 0);
    }
}
