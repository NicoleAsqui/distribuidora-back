package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PapelForroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PapelForroRepository extends JpaRepository<PapelForroEntity, Long> {
    List<PapelForroEntity> findAllByOrderByOrdenAscNombreAsc();
    List<PapelForroEntity> findByActivoTrueOrderByOrdenAscNombreAsc();
}
