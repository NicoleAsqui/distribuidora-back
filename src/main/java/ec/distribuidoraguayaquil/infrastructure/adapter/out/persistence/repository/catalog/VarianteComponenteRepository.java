package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteComponenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VarianteComponenteRepository extends JpaRepository<VarianteComponenteEntity, Long> {
    List<VarianteComponenteEntity> findByVarianteIdOrderByIdAsc(Long varianteId);
}
