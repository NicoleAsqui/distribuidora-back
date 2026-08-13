package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ConfiguracionInteriorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfiguracionInteriorRepository extends JpaRepository<ConfiguracionInteriorEntity, Long> {
    List<ConfiguracionInteriorEntity> findAllByOrderByNombreAsc();
}
