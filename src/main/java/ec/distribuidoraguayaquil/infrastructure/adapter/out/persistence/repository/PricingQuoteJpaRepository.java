package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.PricingQuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PricingQuoteJpaRepository extends JpaRepository<PricingQuoteEntity, String> {

    @Query("select e from PricingQuoteEntity e order by e.createdAt desc")
    List<PricingQuoteEntity> findAllOrderedByCreatedAtDesc();
}
