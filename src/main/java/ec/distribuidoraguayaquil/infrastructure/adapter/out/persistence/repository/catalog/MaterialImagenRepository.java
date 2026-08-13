package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MaterialImagenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialImagenRepository extends JpaRepository<MaterialImagenEntity, Long> {
    List<MaterialImagenEntity> findByMaterialIdOrderByPrincipalDescOrdenAscIdAsc(Long materialId);
}
