package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

/** Modelo / diseño para galería (foto + nombre, sin medidas). */
public record DisenoCardDto(
        Long id,
        String nombre,
        String slug,
        String descripcion,
        Integer orden,
        String image,
        String imageThumb,
        long medidasCount
) {
}
