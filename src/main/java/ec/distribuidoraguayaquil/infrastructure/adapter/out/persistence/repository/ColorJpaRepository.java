package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.ColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColorJpaRepository extends JpaRepository<ColorEntity, String> {
}
