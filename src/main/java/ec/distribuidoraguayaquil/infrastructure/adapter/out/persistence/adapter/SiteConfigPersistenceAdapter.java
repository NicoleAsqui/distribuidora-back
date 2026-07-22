package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.adapter;

import ec.distribuidoraguayaquil.domain.model.SiteConfig;
import ec.distribuidoraguayaquil.domain.port.out.SiteConfigRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.SiteConfigEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.SiteConfigJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SiteConfigPersistenceAdapter implements SiteConfigRepositoryPort {

    private static final String DEFAULT_ID = "default";
    private final SiteConfigJpaRepository repository;

    public SiteConfigPersistenceAdapter(SiteConfigJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<SiteConfig> find() {
        return repository.findById(DEFAULT_ID).map(this::toDomain);
    }

    @Override
    public SiteConfig save(SiteConfig config) {
        SiteConfigEntity e = new SiteConfigEntity();
        e.setId(DEFAULT_ID);
        e.setWhatsappAdmin(config.whatsappAdmin());
        e.setWhatsappAsesora(config.whatsappAsesora());
        e.setEmailAsesor(config.emailAsesor());
        e.setMinLeadDays(config.minLeadDays());
        e.setMapsUrl(config.mapsUrl());
        e.setMapsEmbed(config.mapsEmbed());
        return toDomain(repository.save(e));
    }

    private SiteConfig toDomain(SiteConfigEntity e) {
        return new SiteConfig(e.getId(), e.getWhatsappAdmin(), e.getWhatsappAsesora(),
                e.getEmailAsesor(), e.getMinLeadDays(), e.getMapsUrl(), e.getMapsEmbed());
    }
}
