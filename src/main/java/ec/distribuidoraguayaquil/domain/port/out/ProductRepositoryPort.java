package ec.distribuidoraguayaquil.domain.port.out;

import ec.distribuidoraguayaquil.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    List<Product> findAll();
    List<Product> findActive();
    List<Product> findTop();
    Optional<Product> findByRef(String ref);
    Product save(Product product);
    void deleteById(String id);
}
