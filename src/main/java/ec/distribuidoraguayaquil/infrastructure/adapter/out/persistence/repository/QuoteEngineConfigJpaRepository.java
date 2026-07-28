package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.QuoteEngineConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuoteEngineConfigJpaRepository extends JpaRepository<QuoteEngineConfigEntity, String> {
}
