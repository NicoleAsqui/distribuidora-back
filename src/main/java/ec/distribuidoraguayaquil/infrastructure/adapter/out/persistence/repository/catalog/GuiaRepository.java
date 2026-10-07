package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.GuiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuiaRepository extends JpaRepository<GuiaEntity, Long> {
    List<GuiaEntity> findBySeccionOrderByOrdenAscIdAsc(String seccion);

    List<GuiaEntity> findBySeccionAndActivoTrueOrderByOrdenAscIdAsc(String seccion);

    Optional<GuiaEntity> findBySeccionAndSlug(String seccion, String slug);
}
