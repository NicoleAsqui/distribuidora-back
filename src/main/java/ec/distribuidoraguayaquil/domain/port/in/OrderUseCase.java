package ec.distribuidoraguayaquil.domain.port.in;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;

import java.util.List;

public interface OrderUseCase {
    List<Order> listAll();
    Order getById(String id);
    Order create(Order order);
    Order updateStatus(String id, RequestStatus status);
}
