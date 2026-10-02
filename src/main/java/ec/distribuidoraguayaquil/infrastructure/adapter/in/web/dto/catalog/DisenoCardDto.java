package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

/** Modelo / diseño para galería (foto + nombre, sin medidas). */
public record DisenoCardDto(
        Long id,
        String nombre,
        String slug,
        String descripcion,
        Integer orden,
        /** Familia del diseño: acetato | cartulina | mdf | carton */
        String seccion,
        /** Motor de precios (quote-engine). */
        String motor,
        String image,
        String imageThumb,
        java.util.List<DisenoImagenCardDto> imagenes,
        long medidasCount
) {
}
