package ec.distribuidoraguayaquil.domain.model;

import java.util.List;

public record BoxModel(
        String id,
        String name,
        String categoryId,
        String description,
        List<String> photos,
        List<String> materials,
        List<String> finishes,
        List<String> colors,
        int minQty,
        int leadDays,
        List<String> tags,
        boolean active
) {
}
