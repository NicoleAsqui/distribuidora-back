package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.PricingQuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingQuoteJpaRepository extends JpaRepository<PricingQuoteEntity, String> {
    List<PricingQuoteEntity> findAllByOrderByCreatedAtDesc();
}
