package ec.distribuidoraguayaquil.domain.port.out;

import ec.distribuidoraguayaquil.domain.model.SiteConfig;

import java.util.Optional;

public interface SiteConfigRepositoryPort {
    Optional<SiteConfig> find();
    SiteConfig save(SiteConfig config);
}
