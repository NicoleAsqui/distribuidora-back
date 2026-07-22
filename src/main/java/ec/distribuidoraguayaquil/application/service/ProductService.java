package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.model.Product;
import ec.distribuidoraguayaquil.domain.port.in.ProductUseCase;
import ec.distribuidoraguayaquil.domain.port.out.ProductRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService implements ProductUseCase {

    private final ProductRepositoryPort productRepository;

    public ProductService(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> listPublic(boolean onlyTop) {
        return onlyTop ? productRepository.findTop() : productRepository.findActive();
    }

    @Override
    public Product getByRef(String ref) {
        return productRepository.findByRef(ref)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + ref));
    }

    @Override
    public List<Product> listAll() {
        return productRepository.findAll();
    }

    @Override
    public Product create(Product product) {
        String id = product.id() == null || product.id().isBlank() ? UUID.randomUUID().toString() : product.id();
        Product toSave = new Product(id, product.ref(), product.name(), product.category(),
                product.shortDesc(), product.top(), product.variants(), product.active());
        return productRepository.save(toSave);
    }

    @Override
    public Product update(String id, Product product) {
        productRepository.findAll().stream()
                .filter(p -> p.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        return productRepository.save(new Product(id, product.ref(), product.name(), product.category(),
                product.shortDesc(), product.top(), product.variants(), product.active()));
    }

    @Override
    public void delete(String id) {
        productRepository.deleteById(id);
    }
}
