package ec.distribuidoraguayaquil.infrastructure.config;

import ec.distribuidoraguayaquil.domain.model.BoxModel;
import ec.distribuidoraguayaquil.domain.model.Category;
import ec.distribuidoraguayaquil.domain.model.Color;
import ec.distribuidoraguayaquil.domain.model.Finish;
import ec.distribuidoraguayaquil.domain.model.Material;
import ec.distribuidoraguayaquil.domain.model.SiteConfig;
import ec.distribuidoraguayaquil.domain.model.Tag;
import ec.distribuidoraguayaquil.domain.port.out.BoxModelRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.CategoryRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.ColorRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.FinishRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.MaterialRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.SiteConfigRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.TagRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Solo catálogo auxiliar y config. Los productos se crean desde Admin + imágenes GCS.
 */
@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoryRepositoryPort categoryRepository;
    private final MaterialRepositoryPort materialRepository;
    private final ColorRepositoryPort colorRepository;
    private final FinishRepositoryPort finishRepository;
    private final TagRepositoryPort tagRepository;
    private final BoxModelRepositoryPort boxModelRepository;
    private final SiteConfigRepositoryPort siteConfigRepository;

    public DataSeeder(
            CategoryRepositoryPort categoryRepository,
            MaterialRepositoryPort materialRepository,
            ColorRepositoryPort colorRepository,
            FinishRepositoryPort finishRepository,
            TagRepositoryPort tagRepository,
            BoxModelRepositoryPort boxModelRepository,
            SiteConfigRepositoryPort siteConfigRepository) {
        this.categoryRepository = categoryRepository;
        this.materialRepository = materialRepository;
        this.colorRepository = colorRepository;
        this.finishRepository = finishRepository;
        this.tagRepository = tagRepository;
        this.boxModelRepository = boxModelRepository;
        this.siteConfigRepository = siteConfigRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (categoryRepository.findAll().isEmpty()) {
            seedAdminCatalog();
            log.info("Seed: catálogo admin (sin productos)");
        } else {
            ensureProductColors();
        }
        if (siteConfigRepository.find().isEmpty()) {
            siteConfigRepository.save(new SiteConfig(
                    "default",
                    "593998559865",
                    "593983867216",
                    "jeymyunica@hotmail.com",
                    14,
                    "https://maps.app.goo.gl/be8zucedcysfhB598",
                    "https://www.google.com/maps?q=Guayaquil,Ecuador&output=embed"
            ));
            log.info("Seed: configuración del sitio");
        }
    }

    private void ensureProductColors() {
        var existing = colorRepository.findAll().stream().map(Color::name).collect(java.util.stream.Collectors.toSet());
        if (!existing.contains("Rosa")) {
            colorRepository.save(new Color("cl9", "Rosa", "#f6c6c0", true));
        }
        if (!existing.contains("Azul")) {
            colorRepository.save(new Color("cl10", "Azul", "#cfd9ee", true));
        }
    }

    private void seedAdminCatalog() {
        categoryRepository.save(new Category("c1", "Regalos", "regalos"));
        categoryRepository.save(new Category("c2", "Corporativas", "corporativas"));
        categoryRepository.save(new Category("c3", "Botellas", "botellas"));
        categoryRepository.save(new Category("c4", "Cosméticos", "cosmeticos"));
        categoryRepository.save(new Category("c5", "Alimentos", "alimentos"));
        categoryRepository.save(new Category("c6", "Eventos", "eventos"));
        categoryRepository.save(new Category("c7", "Joyería", "joyeria"));

        materialRepository.save(new Material("m1", "Cartulina", List.of("Blanca", "Kraft")));
        materialRepository.save(new Material("m2", "Cartón", List.of("Con forro", "Sin forro")));
        materialRepository.save(new Material("m3", "Madera", List.of("Natural", "Barnizada")));

        colorRepository.save(new Color("cl1", "Negro", "#111111", true));
        colorRepository.save(new Color("cl2", "Blanco", "#f5f5f0", true));
        colorRepository.save(new Color("cl3", "Kraft", "#c8a978", true));
        colorRepository.save(new Color("cl4", "Dorado", "#d4af37", true));
        colorRepository.save(new Color("cl5", "Plateado", "#c0c0c0", true));
        colorRepository.save(new Color("cl6", "Azul Marino", "#1e2a4a", true));
        colorRepository.save(new Color("cl7", "Rojo", "#c62828", true));
        colorRepository.save(new Color("cl8", "Verde", "#cfe7d8", true));
        colorRepository.save(new Color("cl9", "Rosa", "#f6c6c0", true));
        colorRepository.save(new Color("cl10", "Azul", "#cfd9ee", true));

        finishRepository.save(new Finish("f1", "Papel", "Forro de papel decorativo"));
        finishRepository.save(new Finish("f2", "Vinil", "Forro de vinil texturizado"));
        finishRepository.save(new Finish("f3", "Full Color", "Impresión CMYK a todo color"));
        finishRepository.save(new Finish("f4", "Foil Dorado", "Estampado metalizado dorado"));
        finishRepository.save(new Finish("f5", "Foil Plateado", "Estampado metalizado plateado"));
        finishRepository.save(new Finish("f6", "Grabado Láser", "Grabado por láser en madera"));
        finishRepository.save(new Finish("f7", "UV", "Barniz UV localizado"));
        finishRepository.save(new Finish("f8", "Barniz", "Barniz brillo o mate"));

        tagRepository.save(new Tag("t1", "Premium", "#d4af37"));
        tagRepository.save(new Tag("t2", "Lujo", "#111"));
        tagRepository.save(new Tag("t3", "Magnética", "#00B8C4"));
        tagRepository.save(new Tag("t4", "Regalos", "#f6c6c0"));
        tagRepository.save(new Tag("t5", "Flores", "#e91e63"));
        tagRepository.save(new Tag("t6", "Chocolates", "#795548"));
        tagRepository.save(new Tag("t7", "Corporativo", "#1e2a4a"));
        tagRepository.save(new Tag("t8", "Navidad", "#c62828"));
        tagRepository.save(new Tag("t9", "Minimalista", "#607d8b"));

        boxModelRepository.save(new BoxModel("mo1", "Caja Magnética", "c1",
                "Cierre magnético premium, ideal para regalos corporativos.",
                List.of(), List.of("m2"), List.of("f1", "f3", "f4"), List.of("cl1", "cl2", "cl4"),
                25, 21, List.of("t1", "t3", "t7"), true));
        boxModelRepository.save(new BoxModel("mo2", "Caja Tipo Libro", "c1",
                "Apertura tipo libro con imán lateral.",
                List.of(), List.of("m2"), List.of("f1", "f2"), List.of("cl1", "cl3"),
                20, 18, List.of("t1", "t2"), true));
    }
}
