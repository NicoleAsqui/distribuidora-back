package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialRepository extends JpaRepository<MaterialEntity, Long> {
    List<MaterialEntity> findAllByOrderByOrdenAscNombreAsc();
    List<MaterialEntity> findByActivoTrueOrderByOrdenAscNombreAsc();
    List<MaterialEntity> findByTipoMaterialIdOrderByOrdenAscNombreAsc(Long tipoMaterialId);
}
