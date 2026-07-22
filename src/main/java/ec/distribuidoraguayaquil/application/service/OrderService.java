package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.OrderItem;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import ec.distribuidoraguayaquil.domain.port.in.OrderUseCase;
import ec.distribuidoraguayaquil.domain.port.out.OrderRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService implements OrderUseCase {

    private final OrderRepositoryPort repository;

    public OrderService(OrderRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<Order> listAll() {
        return repository.findAll();
    }

    @Override
    public Order getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }

    @Override
    public Order create(Order order) {
        List<OrderItem> items = order.items() == null ? List.of() : order.items();
        BigDecimal total = items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.qty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long seq = repository.nextSequence();
        Order toSave = new Order(
                UUID.randomUUID().toString(),
                "PED-" + seq,
                order.clientName(),
                order.clientEmail(),
                order.clientPhone(),
                order.notes(),
                items,
                total,
                RequestStatus.PENDING,
                Instant.now()
        );
        return repository.save(toSave);
    }

    @Override
    public Order updateStatus(String id, RequestStatus status) {
        Order existing = getById(id);
        return repository.save(new Order(
                existing.id(), existing.code(), existing.clientName(), existing.clientEmail(),
                existing.clientPhone(), existing.notes(), existing.items(), existing.total(),
                status, existing.createdAt()));
    }
}
