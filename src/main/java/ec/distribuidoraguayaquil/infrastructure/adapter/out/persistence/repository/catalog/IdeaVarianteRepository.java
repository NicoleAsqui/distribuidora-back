package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaVarianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface IdeaVarianteRepository extends JpaRepository<IdeaVarianteEntity, Long> {
    List<IdeaVarianteEntity> findByIdeaIdOrderByOrdenAscIdAsc(Long ideaId);
    List<IdeaVarianteEntity> findByIdeaIdInOrderByOrdenAscIdAsc(Collection<Long> ideaIds);
    List<IdeaVarianteEntity> findByVarianteId(Long varianteId);

    @Query("""
            SELECT iv.ideaId, COUNT(iv.id) FROM IdeaVarianteEntity iv, VarianteEntity v, IdeaEntity i
            WHERE iv.varianteId = v.id
              AND iv.ideaId = i.id
              AND v.activo = TRUE
              AND i.activo = TRUE
            GROUP BY iv.ideaId
            """)
    List<Object[]> countActiveProductsGroupedByIdeaId();
}
