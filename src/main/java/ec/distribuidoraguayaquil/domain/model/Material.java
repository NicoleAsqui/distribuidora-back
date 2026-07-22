package ec.distribuidoraguayaquil.domain.model;

import java.util.List;

public record Material(
        String id,
        String name,
        List<String> options
) {
}
