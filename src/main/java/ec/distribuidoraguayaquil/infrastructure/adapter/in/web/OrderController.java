package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.OrderItem;
import ec.distribuidoraguayaquil.domain.port.in.OrderUseCase;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.StatusUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @GetMapping
    public List<Order> list() {
        return orderUseCase.listAll();
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable String id) {
        return orderUseCase.getById(id);
    }

    @PostMapping
    public Order create(@Valid @RequestBody CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream()
                .map(i -> new OrderItem(i.productRef(), i.name(), i.size(), i.color(), i.price(), i.qty()))
                .toList();
        return orderUseCase.create(new Order(
                null, null, request.clientName(), request.clientEmail(), request.clientPhone(),
                request.notes(), items, null, null, null));
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        return orderUseCase.updateStatus(id, request.status());
    }
}
