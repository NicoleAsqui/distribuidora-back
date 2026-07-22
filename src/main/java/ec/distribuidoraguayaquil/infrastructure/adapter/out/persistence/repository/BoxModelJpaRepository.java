package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.BoxModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoxModelJpaRepository extends JpaRepository<BoxModelEntity, String> {
    List<BoxModelEntity> findByActiveTrue();
}
