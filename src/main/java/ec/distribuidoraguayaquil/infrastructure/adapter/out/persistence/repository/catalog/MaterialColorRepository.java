package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MaterialColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialColorRepository extends JpaRepository<MaterialColorEntity, MaterialColorEntity.Key> {
    List<MaterialColorEntity> findByMaterialId(Long materialId);
    List<MaterialColorEntity> findByColorId(Long colorId);
}
