package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ComponenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComponenteRepository extends JpaRepository<ComponenteEntity, Long> {
    List<ComponenteEntity> findAllByOrderByNombreAsc();
}
