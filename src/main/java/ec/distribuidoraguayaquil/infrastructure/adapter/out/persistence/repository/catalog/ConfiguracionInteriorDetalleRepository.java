package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ConfiguracionInteriorDetalleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfiguracionInteriorDetalleRepository
        extends JpaRepository<ConfiguracionInteriorDetalleEntity, Long> {
    List<ConfiguracionInteriorDetalleEntity> findByConfiguracionIdOrderByIdAsc(Long configuracionId);
}
