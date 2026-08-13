package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IdeaRepository extends JpaRepository<IdeaEntity, Long> {
    List<IdeaEntity> findAllByOrderByOrdenAscIdAsc();
    List<IdeaEntity> findByActivoTrueOrderByOrdenAscIdAsc();
    Optional<IdeaEntity> findBySlug(String slug);
}
