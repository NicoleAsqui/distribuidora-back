package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaVarianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface IdeaVarianteRepository extends JpaRepository<IdeaVarianteEntity, Long> {
    List<IdeaVarianteEntity> findByIdeaIdOrderByOrdenAscIdAsc(Long ideaId);
    List<IdeaVarianteEntity> findByIdeaIdInOrderByOrdenAscIdAsc(Collection<Long> ideaIds);
    List<IdeaVarianteEntity> findByVarianteId(Long varianteId);
}
