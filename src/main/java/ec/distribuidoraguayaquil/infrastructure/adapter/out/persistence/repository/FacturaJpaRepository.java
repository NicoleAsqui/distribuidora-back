package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.FacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacturaJpaRepository extends JpaRepository<FacturaEntity, String> {

    List<FacturaEntity> findAllByOrderByCreatedAtDesc();

    List<FacturaEntity> findByOrderIdOrderByCreatedAtDesc(String orderId);

    @Query("select coalesce(max(f.secuencial), 0) from FacturaEntity f where f.ambiente = ?1")
    int maxSecuencial(String ambiente);
}
