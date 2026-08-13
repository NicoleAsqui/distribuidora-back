package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteImagenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface VarianteImagenRepository extends JpaRepository<VarianteImagenEntity, Long> {
    List<VarianteImagenEntity> findByVarianteIdOrderByPrincipalDescOrdenAscIdAsc(Long varianteId);
    List<VarianteImagenEntity> findByVarianteIdInOrderByPrincipalDescOrdenAscIdAsc(Collection<Long> varianteIds);
}
