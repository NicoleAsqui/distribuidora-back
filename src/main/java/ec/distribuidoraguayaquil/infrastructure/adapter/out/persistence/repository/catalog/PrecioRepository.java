package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PrecioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PrecioRepository extends JpaRepository<PrecioEntity, Long> {
    List<PrecioEntity> findByVarianteIdOrderByCantidadDesdeAsc(Long varianteId);
    List<PrecioEntity> findByVarianteIdInOrderByCantidadDesdeAsc(Collection<Long> varianteIds);
}
