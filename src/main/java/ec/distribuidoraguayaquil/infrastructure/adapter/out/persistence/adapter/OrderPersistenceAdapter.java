package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.adapter;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.OrderItem;
import ec.distribuidoraguayaquil.domain.port.out.OrderRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.OrderEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.OrderItemEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderPersistenceAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository repository;

    public OrderPersistenceAdapter(OrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Order> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Order> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = repository.findById(order.id()).orElseGet(OrderEntity::new);
        entity.setId(order.id());
        entity.setCode(order.code());
        entity.setClientName(order.clientName());
        entity.setClientEmail(order.clientEmail());
        entity.setClientPhone(order.clientPhone());
        entity.setNotes(order.notes());
        entity.setTotal(order.total());
        entity.setStatus(order.status());
        entity.setCreatedAt(order.createdAt());
        entity.getItems().clear();
        if (order.items() != null) {
            for (OrderItem item : order.items()) {
                OrderItemEntity ie = new OrderItemEntity();
                ie.setOrder(entity);
                ie.setProductRef(item.productRef());
                ie.setName(item.name());
                ie.setSize(item.size());
                ie.setColor(item.color());
                ie.setPrice(item.price());
                ie.setQty(item.qty());
                entity.getItems().add(ie);
            }
        }
        return toDomain(repository.save(entity));
    }

    @Override
    public long nextSequence() {
        return 1000 + repository.countAll() + 1;
    }

    private Order toDomain(OrderEntity e) {
        List<OrderItem> items = e.getItems().stream()
                .map(i -> new OrderItem(i.getProductRef(), i.getName(), i.getSize(), i.getColor(), i.getPrice(), i.getQty()))
                .toList();
        return new Order(e.getId(), e.getCode(), e.getClientName(), e.getClientEmail(), e.getClientPhone(),
                e.getNotes(), items, e.getTotal(), e.getStatus(), e.getCreatedAt());
    }
}
