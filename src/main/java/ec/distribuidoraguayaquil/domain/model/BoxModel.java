package ec.distribuidoraguayaquil.domain.model;

import java.util.List;

public record BoxModel(
        String id,
        String name,
        /** Categoría principal (compat); preferir categoryIds. */
        String categoryId,
        /** Usos / categorías múltiples: regalos, joyería, etc. */
        List<String> categoryIds,
        String description,
        List<String> photos,
        /** Materiales aplicables (idealmente 1: cartulina / cartón / madera). */
        List<String> materials,
        List<String> finishes,
        List<String> colors,
        int minQty,
        int leadDays,
        List<String> tags,
        boolean active
) {
}
