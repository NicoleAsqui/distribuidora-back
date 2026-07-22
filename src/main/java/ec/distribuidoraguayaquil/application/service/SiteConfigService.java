package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.model.SiteConfig;
import ec.distribuidoraguayaquil.domain.port.in.SiteConfigUseCase;
import ec.distribuidoraguayaquil.domain.port.out.SiteConfigRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class SiteConfigService implements SiteConfigUseCase {

    private final SiteConfigRepositoryPort repository;

    public SiteConfigService(SiteConfigRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public SiteConfig get() {
        return repository.find().orElseGet(() -> new SiteConfig(
                "default",
                "593998559865",
                "593983867216",
                "jeymyunica@hotmail.com",
                14,
                "https://maps.app.goo.gl/be8zucedcysfhB598",
                "https://www.google.com/maps?q=Guayaquil,Ecuador&output=embed"
        ));
    }

    @Override
    public SiteConfig update(SiteConfig config) {
        SiteConfig current = get();
        return repository.save(new SiteConfig(
                current.id(),
                config.whatsappAdmin(),
                config.whatsappAsesora(),
                config.emailAsesor(),
                config.minLeadDays(),
                config.mapsUrl() == null ? current.mapsUrl() : config.mapsUrl(),
                config.mapsEmbed() == null ? current.mapsEmbed() : config.mapsEmbed()
        ));
    }
}
