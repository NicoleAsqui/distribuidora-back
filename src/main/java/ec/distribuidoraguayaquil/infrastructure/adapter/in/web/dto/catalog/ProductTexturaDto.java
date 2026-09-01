package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

/** Color/textura ofrecido en un producto con su foto asociada. */
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
