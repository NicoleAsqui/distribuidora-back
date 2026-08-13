package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog;

/** Forma antigua {id, name, hex, available} que aún consume el frontend en /api/catalog/colors. */
public record LegacyColorDto(
        String id,
        String name,
        String hex,
        boolean available
) {
}
