package ec.distribuidoraguayaquil.infrastructure.config;

import ec.distribuidoraguayaquil.domain.model.BoxModel;
import ec.distribuidoraguayaquil.domain.model.Category;
import ec.distribuidoraguayaquil.domain.model.Color;
import ec.distribuidoraguayaquil.domain.model.Finish;
import ec.distribuidoraguayaquil.domain.model.Material;
import ec.distribuidoraguayaquil.domain.model.Product;
import ec.distribuidoraguayaquil.domain.model.ProductVariant;
import ec.distribuidoraguayaquil.domain.model.QuoteRequest;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import ec.distribuidoraguayaquil.domain.model.SiteConfig;
import ec.distribuidoraguayaquil.domain.model.Tag;
import ec.distribuidoraguayaquil.domain.port.out.BoxModelRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.CategoryRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.ColorRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.FinishRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.MaterialRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.ProductRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.QuoteRequestRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.SiteConfigRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.TagRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final ProductRepositoryPort productRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final MaterialRepositoryPort materialRepository;
    private final ColorRepositoryPort colorRepository;
    private final FinishRepositoryPort finishRepository;
    private final TagRepositoryPort tagRepository;
    private final BoxModelRepositoryPort boxModelRepository;
    private final QuoteRequestRepositoryPort quoteRequestRepository;
    private final SiteConfigRepositoryPort siteConfigRepository;

    public DataSeeder(
            ProductRepositoryPort productRepository,
            CategoryRepositoryPort categoryRepository,
            MaterialRepositoryPort materialRepository,
            ColorRepositoryPort colorRepository,
            FinishRepositoryPort finishRepository,
            TagRepositoryPort tagRepository,
            BoxModelRepositoryPort boxModelRepository,
            QuoteRequestRepositoryPort quoteRequestRepository,
            SiteConfigRepositoryPort siteConfigRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.materialRepository = materialRepository;
        this.colorRepository = colorRepository;
        this.finishRepository = finishRepository;
        this.tagRepository = tagRepository;
        this.boxModelRepository = boxModelRepository;
        this.quoteRequestRepository = quoteRequestRepository;
        this.siteConfigRepository = siteConfigRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (productRepository.findAll().isEmpty()) {
            seedProducts();
            log.info("Seed: productos del catálogo público");
        }
        if (categoryRepository.findAll().isEmpty()) {
            seedAdminCatalog();
            log.info("Seed: catálogo admin");
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
        if (quoteRequestRepository.findAll().isEmpty()) {
            seedQuotes();
            log.info("Seed: cotizaciones de ejemplo");
        }
    }

    private void seedProducts() {
        productRepository.save(new Product("p2213", "2213", "Caja rectangular automontable", "Cajas para envío",
                "Caja de cartón automontable, ideal para envíos rápidos.", true, List.of(
                v("13x8x3 cm", "Blanco", "0.48", null),
                v("15x10x6 cm", "Blanco", "0.69", null),
                v("20x10x5 cm", "Blanco", "0.85", "HOT"),
                v("20x16x6 cm", "Kraft", "1.10", null),
                v("25x15x4 cm", "Kraft", "1.25", "HOT")
        ), true));
        productRepository.save(new Product("p2262", "2262", "Caja cuadrada para envíos", "Cajas para envío",
                "Caja cuadrada de cartón para productos medianos.", true, List.of(
                v("15x15x5 cm", "Blanco", "0.87", null),
                v("15x15x5 cm", "Kraft", "0.87", null),
                v("20x20x6 cm", "Blanco", "1.15", null),
                v("20x20x6 cm", "Kraft", "1.15", null)
        ), true));
        productRepository.save(new Product("p2293", "2293", "Caja postal kraft resistente", "Cajas para envío",
                "Caja postal en kraft, resistente y lista para ecommerce.", true, List.of(
                v("25x17x8 cm", "Kraft", "0.93", null),
                v("30x20x10 cm", "Kraft", "1.45", null)
        ), true));
        productRepository.save(new Product("p2519", "2519", "Caja automontable con tapa", "Cajas regalo",
                "Caja automontable con tapa para regalo y presentación premium.", true, List.of(
                v("18x10x4 cm", "Blanco", "0.77", null),
                v("20x14x6 cm", "Blanco", "0.95", null),
                v("20x14x6 cm", "Negro", "1.05", null)
        ), true));
        productRepository.save(new Product("p3110", "3110", "Caja joyería terciopelo", "Cajas joyería",
                "Caja pequeña forrada para joyería y detalles.", false, List.of(
                v("7x7x3 cm", "Negro", "1.40", null),
                v("9x9x4 cm", "Rosa", "1.60", null)
        ), true));
        productRepository.save(new Product("p4220", "4220", "Caja repostería con visor", "Cajas repostería",
                "Caja para pasteles, cupcakes y postres con ventana transparente.", false, List.of(
                v("20x20x10 cm", "Blanco", "1.20", null),
                v("25x25x12 cm", "Blanco", "1.55", null)
        ), true));
        productRepository.save(new Product("p5301", "5301", "Caja eventos personalizada", "Cajas eventos",
                "Caja para bodas, cumpleaños y detalles de evento.", false, List.of(
                v("10x10x10 cm", "Rosa", "0.95", null),
                v("10x10x10 cm", "Verde", "0.95", null),
                v("12x12x12 cm", "Azul", "1.15", null)
        ), true));
        productRepository.save(new Product("p6402", "6402", "Caja cosmética rígida", "Cajas cosmética",
                "Caja rígida ideal para cosmética y productos premium.", false, List.of(
                v("15x10x5 cm", "Negro", "1.85", null),
                v("18x12x6 cm", "Blanco", "2.10", null)
        ), true));
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
        colorRepository.save(new Color("cl2", "Blanco", "#ffffff", true));
        colorRepository.save(new Color("cl3", "Kraft", "#c8a978", true));
        colorRepository.save(new Color("cl4", "Dorado", "#d4af37", true));
        colorRepository.save(new Color("cl5", "Plateado", "#c0c0c0", true));
        colorRepository.save(new Color("cl6", "Azul Marino", "#1e2a4a", true));
        colorRepository.save(new Color("cl7", "Rojo", "#c62828", true));
        colorRepository.save(new Color("cl8", "Verde", "#2e7d32", true));

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
        boxModelRepository.save(new BoxModel("mo3", "Caja Deslizable", "c4",
                "Caja tipo cajón deslizable para cosméticos.",
                List.of(), List.of("m2"), List.of("f3"), List.of("cl2", "cl5"),
                50, 15, List.of("t9"), true));
        boxModelRepository.save(new BoxModel("mo4", "Caja con Ventana", "c5",
                "Ventana PVC para producto visible.",
                List.of(), List.of("m2"), List.of("f3"), List.of("cl2", "cl3"),
                30, 14, List.of("t6"), true));
        boxModelRepository.save(new BoxModel("mo5", "Caja Hexagonal", "c6",
                "Forma hexagonal para eventos y detalles.",
                List.of(), List.of("m1", "m2"), List.of("f1", "f4"), List.of("cl4", "cl7"),
                25, 20, List.of("t4", "t8"), true));
        boxModelRepository.save(new BoxModel("mo6", "Caja Cilíndrica", "c3",
                "Cilindro para botellas y detalles largos.",
                List.of(), List.of("m2", "m3"), List.of("f6"), List.of("cl3", "cl6"),
                15, 25, List.of("t1"), false));
    }

    private void seedQuotes() {
        quoteRequestRepository.save(new QuoteRequest("r1", "COT-1042", "María Torres", "maria@example.com",
                "0991234567", "mo1", "Cartón + forro papel", 50, LocalDate.parse("2026-07-10"),
                RequestStatus.PENDING, "Boda 15 agosto", List.of("logo.pdf")));
        quoteRequestRepository.save(new QuoteRequest("r2", "COT-1041", "Grupo Andina S.A.", "compras@andina.ec",
                "042345678", "mo3", "Cartón blanco", 200, LocalDate.parse("2026-07-08"),
                RequestStatus.SENT, "Kit corporativo Q3", List.of("diseño-v2.pdf", "logo.png")));
        quoteRequestRepository.save(new QuoteRequest("r3", "COT-1040", "Panadería El Trigo", "info@eltrigo.ec",
                "0987654321", "mo4", "Cartón kraft", 500, LocalDate.parse("2026-07-05"),
                RequestStatus.PRODUCTION, "Cajas para pastel", List.of()));
    }

    private static ProductVariant v(String size, String color, String price, String tag) {
        return new ProductVariant(size, color, new BigDecimal(price), tag);
    }
}
