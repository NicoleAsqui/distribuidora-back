package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteAtributoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VarianteAtributoRepository
        extends JpaRepository<VarianteAtributoEntity, VarianteAtributoEntity.Key> {
    List<VarianteAtributoEntity> findByVarianteId(Long varianteId);
    List<VarianteAtributoEntity> findByAtributoValorId(Long atributoValorId);
}
