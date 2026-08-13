package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColorRepository extends JpaRepository<ColorEntity, Long> {
    List<ColorEntity> findAllByOrderByNombreAsc();
    List<ColorEntity> findByActivoTrueOrderByNombreAsc();
}
