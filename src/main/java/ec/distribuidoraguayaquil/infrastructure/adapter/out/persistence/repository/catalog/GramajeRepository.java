package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.GramajeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GramajeRepository extends JpaRepository<GramajeEntity, Long> {
    List<GramajeEntity> findByMaterialIdOrderByValorAsc(Long materialId);
}
