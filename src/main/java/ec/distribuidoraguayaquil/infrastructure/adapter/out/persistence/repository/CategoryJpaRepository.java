package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, String> {
}
