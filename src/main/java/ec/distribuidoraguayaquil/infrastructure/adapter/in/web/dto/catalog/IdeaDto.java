package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

import java.util.List;

public record IdeaDto(
        Long id,
        String nombre,
        String slug,
        String descripcion,
        Boolean activo,
        Integer orden,
        String imagen,
        List<String> imagenes,
        List<IdeaVarianteDto> variantes
) {
    public record IdeaVarianteDto(
            Long id,
            Long varianteId,
            String sku,
            String titulo,
            String descripcion,
            Integer orden
    ) {
    }
}
