package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.AtributoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtributoRepository extends JpaRepository<AtributoEntity, Long> {
    List<AtributoEntity> findAllByOrderByNombreAsc();
}
