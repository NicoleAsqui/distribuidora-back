package ec.distribuidoraguayaquil.domain.port.in;

import ec.distribuidoraguayaquil.domain.model.Product;

import java.util.List;

public interface ProductUseCase {
    List<Product> listPublic(boolean onlyTop);
    Product getByRef(String ref);
    List<Product> listAll();
    Product create(Product product);
    Product update(String id, Product product);
    void delete(String id);
}
