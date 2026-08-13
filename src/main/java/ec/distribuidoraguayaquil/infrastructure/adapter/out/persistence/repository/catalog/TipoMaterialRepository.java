package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.TipoMaterialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoMaterialRepository extends JpaRepository<TipoMaterialEntity, Long> {
    List<TipoMaterialEntity> findAllByOrderByNombreAsc();
}
