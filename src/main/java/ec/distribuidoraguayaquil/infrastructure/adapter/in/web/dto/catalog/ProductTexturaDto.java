package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

/** Color/textura del diseño con foto del modelo en ese color (detalle de producto). */
public record ProductTexturaDto(
        Long texturaId,
        String nombre,
        String slug,
        String swatchUrl,
        String swatchThumbUrl,
        String image,
        String imageThumb,
        int orden
) {
}
