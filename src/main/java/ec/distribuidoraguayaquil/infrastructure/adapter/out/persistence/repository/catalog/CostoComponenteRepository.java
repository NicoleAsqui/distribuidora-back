package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.CostoComponenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CostoComponenteRepository extends JpaRepository<CostoComponenteEntity, Long> {
    Optional<CostoComponenteEntity> findByVarianteComponenteId(Long varianteComponenteId);
}
