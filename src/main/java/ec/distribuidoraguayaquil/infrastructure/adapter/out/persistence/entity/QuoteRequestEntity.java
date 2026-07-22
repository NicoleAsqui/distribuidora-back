package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quote_requests")
public class QuoteRequestEntity {
    @Id
    private String id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String client;
    private String email;
    private String phone;
    @Column(name = "model_id")
    private String modelId;
    private String material;
    @Column(nullable = false)
    private int qty;
    @Column(nullable = false)
    private LocalDate requestDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    @Column(length = 2000)
    private String notes;
    @Convert(converter = StringListConverter.class)
    @Column(length = 4000)
    private List<String> attachments = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getClient() { return client; }
    public void setClient(String client) { this.client = client; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<String> getAttachments() { return attachments; }
    public void setAttachments(List<String> attachments) { this.attachments = attachments; }
}
