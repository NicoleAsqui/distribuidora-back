package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

import java.util.List;

public record GuiaDto(
        Long id,
        String seccion,
        String slug,
        String titulo,
        String resumen,
        String etiqueta,
        String cuerpo,
        String videoUrl,
        Boolean activo,
        Integer orden,
        List<Imagen> imagenes
) {
    public record Imagen(Long id, String url, String urlThumb, Integer orden) {}
}
