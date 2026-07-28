package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.domain.model.Color;
import ec.distribuidoraguayaquil.domain.port.in.CatalogAdminUseCase;
import ec.distribuidoraguayaquil.domain.port.out.ProductRepositoryPort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/catalog")
public class PublicCatalogController {

    private final ProductRepositoryPort productRepository;
    private final CatalogAdminUseCase catalogAdminUseCase;

    public PublicCatalogController(ProductRepositoryPort productRepository, CatalogAdminUseCase catalogAdminUseCase) {
        this.productRepository = productRepository;
        this.catalogAdminUseCase = catalogAdminUseCase;
    }

    @GetMapping("/product-categories")
    public List<String> productCategories() {
        Set<String> cats = new LinkedHashSet<>();
        productRepository.findActive().stream()
                .sorted(Comparator.comparing(p -> p.category() == null ? "" : p.category()))
                .forEach(p -> {
                    if (p.category() != null && !p.category().isBlank()) {
                        cats.add(p.category());
                    }
                });
        return List.copyOf(cats);
    }

    @GetMapping("/colors")
    public List<Color> colors() {
        return catalogAdminUseCase.listColors().stream()
                .filter(Color::available)
                .toList();
    }
}
