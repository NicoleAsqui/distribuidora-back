package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.adapter;

import ec.distribuidoraguayaquil.domain.model.Product;
import ec.distribuidoraguayaquil.domain.model.ProductVariant;
import ec.distribuidoraguayaquil.domain.port.out.ProductRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.ProductEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.ProductVariantEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository repository;

    public ProductPersistenceAdapter(ProductJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Product> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Product> findActive() {
        return repository.findByActiveTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Product> findTop() {
        return repository.findByActiveTrueAndTopProductTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Product> findByRef(String ref) {
        return repository.findByRef(ref).map(this::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = repository.findById(product.id()).orElseGet(ProductEntity::new);
        entity.setId(product.id());
        entity.setRef(product.ref());
        entity.setName(product.name());
        entity.setCategory(product.category());
        entity.setShortDesc(product.shortDesc());
        entity.setTopProduct(product.top());
        entity.setActive(product.active());
        entity.getVariants().clear();
        if (product.variants() != null) {
            for (ProductVariant v : product.variants()) {
                ProductVariantEntity ve = new ProductVariantEntity();
                ve.setProduct(entity);
                ve.setSize(v.size());
                ve.setColor(v.color());
                ve.setPrice(v.price());
                ve.setTag(v.tag());
                entity.getVariants().add(ve);
            }
        }
        return toDomain(repository.save(entity));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private Product toDomain(ProductEntity e) {
        List<ProductVariant> variants = e.getVariants().stream()
                .map(v -> new ProductVariant(v.getSize(), v.getColor(), v.getPrice(), v.getTag()))
                .toList();
        return new Product(e.getId(), e.getRef(), e.getName(), e.getCategory(), e.getShortDesc(),
                e.isTopProduct(), variants, e.isActive());
    }
}
