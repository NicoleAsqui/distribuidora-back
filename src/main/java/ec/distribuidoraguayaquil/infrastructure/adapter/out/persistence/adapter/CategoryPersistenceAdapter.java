package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.adapter;

import ec.distribuidoraguayaquil.domain.model.Category;
import ec.distribuidoraguayaquil.domain.port.out.CategoryRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.CategoryJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CategoryPersistenceAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository repository;

    public CategoryPersistenceAdapter(CategoryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Category> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Category save(Category category) {
        CategoryEntity e = new CategoryEntity();
        e.setId(category.id());
        e.setName(category.name());
        e.setSlug(category.slug());
        return toDomain(repository.save(e));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private Category toDomain(CategoryEntity e) {
        return new Category(e.getId(), e.getName(), e.getSlug());
    }
}
