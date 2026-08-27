package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoImagenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DisenoImagenRepository extends JpaRepository<DisenoImagenEntity, Long> {
    List<DisenoImagenEntity> findByDisenoIdOrderByPrincipalDescOrdenAscIdAsc(Long disenoId);

    List<DisenoImagenEntity> findByDisenoIdInOrderByPrincipalDescOrdenAscIdAsc(Collection<Long> disenoIds);

    void deleteByDisenoId(Long disenoId);
}
