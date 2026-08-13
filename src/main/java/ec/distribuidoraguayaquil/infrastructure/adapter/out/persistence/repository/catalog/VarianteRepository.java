package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VarianteRepository extends JpaRepository<VarianteEntity, Long> {
    Optional<VarianteEntity> findBySku(String sku);
    List<VarianteEntity> findByActivoTrue();
    List<VarianteEntity> findByDisenoIdOrderByIdAsc(Long disenoId);
    List<VarianteEntity> findByDisenoIdAndActivoTrueOrderByIdAsc(Long disenoId);
}
