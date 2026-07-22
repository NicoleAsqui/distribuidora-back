package ec.distribuidoraguayaquil.domain.port.in;

import ec.distribuidoraguayaquil.domain.model.SiteConfig;

public interface SiteConfigUseCase {
    SiteConfig get();
    SiteConfig update(SiteConfig config);
}
