package ec.distribuidoraguayaquil.domain.port.out;

import ec.distribuidoraguayaquil.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {
    List<Category> findAll();
    Optional<Category> findById(String id);
    Category save(Category category);
    void deleteById(String id);
}
