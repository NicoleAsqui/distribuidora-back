package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.domain.model.Product;
import ec.distribuidoraguayaquil.domain.port.in.ProductUseCase;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping
    public List<Product> list(@RequestParam(defaultValue = "false") boolean top) {
        return productUseCase.listPublic(top);
    }

    @GetMapping("/{ref}")
    public Product byRef(@PathVariable String ref) {
        return productUseCase.getByRef(ref);
    }

    @GetMapping("/admin/all")
    public List<Product> listAllAdmin() {
        return productUseCase.listAll();
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productUseCase.create(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable String id, @RequestBody Product product) {
        return productUseCase.update(id, product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        productUseCase.delete(id);
    }
}
