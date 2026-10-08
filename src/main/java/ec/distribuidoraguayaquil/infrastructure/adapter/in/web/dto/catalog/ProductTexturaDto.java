package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

/** Textura o color del diseño (detalle de producto). */
public record ProductTexturaDto(
        Long texturaId,
        String nombre,
        String slug,
        /** textura | color */
        String tipo,
        String swatchUrl,
        String swatchThumbUrl,
        String image,
        String imageThumb,
        int orden
) {
}
