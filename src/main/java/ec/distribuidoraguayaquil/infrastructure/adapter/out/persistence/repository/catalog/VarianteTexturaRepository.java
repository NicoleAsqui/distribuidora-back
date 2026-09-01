package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteTexturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface VarianteTexturaRepository extends JpaRepository<VarianteTexturaEntity, Long> {
    List<VarianteTexturaEntity> findByVarianteIdOrderByOrdenAscIdAsc(Long varianteId);

    List<VarianteTexturaEntity> findByVarianteIdInOrderByOrdenAscIdAsc(Collection<Long> varianteIds);

    void deleteByVarianteId(Long varianteId);
}
