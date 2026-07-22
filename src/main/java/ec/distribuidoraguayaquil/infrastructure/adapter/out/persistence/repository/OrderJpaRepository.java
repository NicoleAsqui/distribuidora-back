package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
    @Query("select count(o) from OrderEntity o")
    long countAll();
}
