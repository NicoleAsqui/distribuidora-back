package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.AtributoValorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AtributoValorRepository extends JpaRepository<AtributoValorEntity, Long> {
    List<AtributoValorEntity> findByAtributoIdOrderByValorAsc(Long atributoId);
}
