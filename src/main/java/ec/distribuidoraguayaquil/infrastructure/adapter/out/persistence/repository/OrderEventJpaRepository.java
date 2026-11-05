package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.OrderEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderEventJpaRepository extends JpaRepository<OrderEventEntity, Long> {
    List<OrderEventEntity> findByOrderIdOrderByCreatedAtAscIdAsc(String orderId);
}
