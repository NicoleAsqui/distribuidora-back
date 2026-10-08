package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ficha completa de producto (variante + precios + imágenes + materiales + tags).
 * La medida se envía como largo/ancho/alto (se crea o reutiliza en servidor).
 * El SKU se asigna automáticamente si viene vacío al crear.
 */
public record ProductoAdminDto(
        Long id,
        Long disenoId,
        Long medidaId,
        BigDecimal largo,
        BigDecimal ancho,
        BigDecimal alto,
        String unidad,
        String sku,
        /** unidad | paquete | carton | caja */
        String unidadVenta,
        /** Unidades por empaque; null si unidad_venta = unidad */
        Integer unidadesContenido,
        Boolean activo,
        List<PrecioLine> precios,
        List<ImagenLine> imagenes,
        List<ComponenteLine> componentes,
        List<Long> tagIds,
        List<TexturaLine> texturas
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

    public record TexturaLine(
            Long id,
            Long texturaId,
            String url,
            String urlThumb,
            Integer orden,
            Boolean colorActivo
    ) {
    }
}
