package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

/** Modelo / diseño para galería (foto + nombre, sin medidas). */
public record DisenoCardDto(
        Long id,
        String nombre,
        String slug,
        String descripcion,
        Integer orden,
        /** Familia visual del catálogo: cartulina | mdf | carton */
        String seccion,
        String image,
        String imageThumb,
        long medidasCount
) {
}
