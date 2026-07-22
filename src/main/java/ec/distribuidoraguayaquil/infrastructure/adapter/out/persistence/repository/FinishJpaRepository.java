package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.FinishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinishJpaRepository extends JpaRepository<FinishEntity, String> {
}
