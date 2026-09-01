package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.TexturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TexturaRepository extends JpaRepository<TexturaEntity, Long> {
    List<TexturaEntity> findAllByOrderByOrdenAscNombreAscIdAsc();

    List<TexturaEntity> findByActivoTrueAndSeccionOrderByOrdenAscNombreAscIdAsc(String seccion);

    List<TexturaEntity> findByIdIn(Collection<Long> ids);

    Optional<TexturaEntity> findBySlug(String slug);
}
