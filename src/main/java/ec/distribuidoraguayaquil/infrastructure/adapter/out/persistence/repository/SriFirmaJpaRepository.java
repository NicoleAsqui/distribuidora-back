package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.SriFirmaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SriFirmaJpaRepository extends JpaRepository<SriFirmaEntity, Integer> {
}
