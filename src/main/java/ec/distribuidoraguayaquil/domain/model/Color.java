package ec.distribuidoraguayaquil.domain.model;

public record Color(
        String id,
        String name,
        String hex,
        boolean available
) {
}
