package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
    @Query("select count(o) from OrderEntity o")
    long countAll();

    /** Máximo número de PED-{n} para generar el siguiente código sin colisiones. */
    @Query(value = """
            SELECT COALESCE(MAX(CAST(SUBSTRING(code FROM 5) AS BIGINT)), 1000)
            FROM orders
            WHERE code ~ '^PED-[0-9]+$'
            """, nativeQuery = true)
    long maxPedSequence();

    Optional<OrderEntity> findByCode(String code);
}
