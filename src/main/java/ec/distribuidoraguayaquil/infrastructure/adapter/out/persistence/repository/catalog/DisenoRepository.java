package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisenoRepository extends JpaRepository<DisenoEntity, Long> {
    List<DisenoEntity> findAllByOrderByOrdenAscIdAsc();
    List<DisenoEntity> findByActivoTrueOrderByOrdenAscIdAsc();
    Optional<DisenoEntity> findBySlug(String slug);
    Optional<DisenoEntity> findByNombre(String nombre);
}
