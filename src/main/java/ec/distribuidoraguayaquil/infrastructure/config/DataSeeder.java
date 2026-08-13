package ec.distribuidoraguayaquil.infrastructure.config;

import ec.distribuidoraguayaquil.domain.model.SiteConfig;
import ec.distribuidoraguayaquil.domain.port.out.SiteConfigRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Solo configuración del sitio. El catálogo (diseños, variantes, materiales, precios…)
 * se carga con los scripts de docs/sql y se administra desde /api/admin/catalog.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final SiteConfigRepositoryPort siteConfigRepository;

    public DataSeeder(SiteConfigRepositoryPort siteConfigRepository) {
        this.siteConfigRepository = siteConfigRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (siteConfigRepository.find().isEmpty()) {
            siteConfigRepository.save(new SiteConfig(
                    "default",
                    "593998559865",
                    "593983867216",
                    "jeymyunica@hotmail.com",
                    14,
                    "https://maps.app.goo.gl/tnnQgcCEQ1Y9raVM6",
                    "https://www.google.com/maps?q=Francisco+J%C3%A1come+Mz+278+Villa+1,+Florida+Norte,+Guayaquil,+Ecuador&output=embed"
            ));
            log.info("Seed: configuración del sitio");
        }
    }
}
