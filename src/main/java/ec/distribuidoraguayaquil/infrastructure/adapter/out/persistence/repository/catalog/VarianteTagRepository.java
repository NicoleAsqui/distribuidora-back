package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface VarianteTagRepository extends JpaRepository<VarianteTagEntity, VarianteTagEntity.Key> {
    List<VarianteTagEntity> findByVarianteId(Long varianteId);
    List<VarianteTagEntity> findByVarianteIdIn(Collection<Long> varianteIds);
    List<VarianteTagEntity> findByTagId(Long tagId);
}
