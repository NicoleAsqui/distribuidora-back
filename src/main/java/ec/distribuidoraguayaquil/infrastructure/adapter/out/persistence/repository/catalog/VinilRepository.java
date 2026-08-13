package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VinilEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VinilRepository extends JpaRepository<VinilEntity, Long> {
    List<VinilEntity> findAllByOrderByOrdenAscNombreAsc();
    List<VinilEntity> findByActivoTrueOrderByOrdenAscNombreAsc();
    List<VinilEntity> findByActivoTrueAndTipoOrderByOrdenAscNombreAsc(String tipo);
}
