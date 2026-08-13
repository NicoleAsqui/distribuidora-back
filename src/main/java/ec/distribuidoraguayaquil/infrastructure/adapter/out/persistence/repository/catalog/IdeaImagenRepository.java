package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaImagenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface IdeaImagenRepository extends JpaRepository<IdeaImagenEntity, Long> {
    List<IdeaImagenEntity> findByIdeaIdOrderByPrincipalDescOrdenAscIdAsc(Long ideaId);
    List<IdeaImagenEntity> findByIdeaIdInOrderByPrincipalDescOrdenAscIdAsc(Collection<Long> ideaIds);
}
