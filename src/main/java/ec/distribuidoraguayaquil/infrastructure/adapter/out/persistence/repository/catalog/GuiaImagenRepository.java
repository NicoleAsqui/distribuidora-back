package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.GuiaImagenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface GuiaImagenRepository extends JpaRepository<GuiaImagenEntity, Long> {
    List<GuiaImagenEntity> findByGuiaIdInOrderByOrdenAscIdAsc(Collection<Long> guiaIds);

    void deleteByGuiaId(Long guiaId);
}
