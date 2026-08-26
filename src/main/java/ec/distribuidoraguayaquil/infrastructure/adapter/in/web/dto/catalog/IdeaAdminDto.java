package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

import java.util.List;

/**
 * Ficha completa de una categoría de idea: datos + fotos + productos vinculados.
 */
public record IdeaAdminDto(
        Long id,
        String nombre,
        String slug,
        String descripcion,
        Boolean activo,
        Integer orden,
        List<ImagenLine> imagenes,
        List<ProductoLine> productos
) {
    public record ImagenLine(
            Long id,
            String url,
            String urlThumb,
            Boolean principal,
            Integer orden
    ) {
    }

    public record ProductoLine(
            Long id,
            Long varianteId,
            String titulo,
            String descripcion,
            Integer orden
    ) {
    }
}
