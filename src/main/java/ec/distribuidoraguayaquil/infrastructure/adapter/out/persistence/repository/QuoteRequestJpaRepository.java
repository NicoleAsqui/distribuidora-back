package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.QuoteRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuoteRequestJpaRepository extends JpaRepository<QuoteRequestEntity, String> {
    @Query("select count(q) from QuoteRequestEntity q")
    long countAll();
}
