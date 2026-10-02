package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoTexturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DisenoTexturaRepository extends JpaRepository<DisenoTexturaEntity, Long> {
    List<DisenoTexturaEntity> findByDisenoIdOrderByOrdenAscIdAsc(Long disenoId);

    List<DisenoTexturaEntity> findByDisenoIdInOrderByOrdenAscIdAsc(Collection<Long> disenoIds);

    void deleteByDisenoId(Long disenoId);
}
