package ec.distribuidoraguayaquil.domain.port.out;

import ec.distribuidoraguayaquil.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {
    List<Order> findAll();
    Optional<Order> findById(String id);
    Order save(Order order);
    long nextSequence();
}
