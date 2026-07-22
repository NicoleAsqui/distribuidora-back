package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.MaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialJpaRepository extends JpaRepository<MaterialEntity, String> {
}
