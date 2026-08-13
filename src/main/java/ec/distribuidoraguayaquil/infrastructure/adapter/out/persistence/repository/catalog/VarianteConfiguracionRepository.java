package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteConfiguracionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VarianteConfiguracionRepository
        extends JpaRepository<VarianteConfiguracionEntity, VarianteConfiguracionEntity.Key> {
    List<VarianteConfiguracionEntity> findByVarianteId(Long varianteId);
    List<VarianteConfiguracionEntity> findByConfiguracionId(Long configuracionId);
}
