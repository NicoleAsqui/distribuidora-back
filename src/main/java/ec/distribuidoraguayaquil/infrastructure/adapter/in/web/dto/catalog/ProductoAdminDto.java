package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ficha completa de producto (variante + precios + imágenes + materiales + tags).
 * El SKU se asigna automáticamente si viene vacío al crear.
 */
public record ProductoAdminDto(
        Long id,
        Long disenoId,
        Long medidaId,
        String sku,
        Boolean activo,
        List<PrecioLine> precios,
        List<ImagenLine> imagenes,
        List<ComponenteLine> componentes,
        List<Long> tagIds
) {
    public record PrecioLine(
            Long id,
            Integer cantidadDesde,
            BigDecimal precio
    ) {
    }

    public record ImagenLine(
            Long id,
            String url,
            String urlThumb,
            Boolean principal,
            Integer orden
    ) {
    }

    public record ComponenteLine(
            Long id,
            Long componenteId,
            Long materialId,
            Long gramajeId,
            BigDecimal cantidad
    ) {
    }
}
