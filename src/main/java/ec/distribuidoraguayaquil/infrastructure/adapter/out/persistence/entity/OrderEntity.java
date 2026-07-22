package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    private String id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(name = "client_name", nullable = false)
    private String clientName;
    @Column(name = "client_email")
    private String clientEmail;
    @Column(name = "client_phone")
    private String clientPhone;
    @Column(length = 2000)
    private String notes;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItemEntity> items = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<OrderItemEntity> getItems() { return items; }
    public void setItems(List<OrderItemEntity> items) { this.items = items; }
}
