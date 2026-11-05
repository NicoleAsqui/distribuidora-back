package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/** Valores secretos de la app (p. ej. token del link privado del calendario). */
@Entity
@Table(name = "app_secrets")
public class AppSecretEntity {
    @Id
    @Column(length = 60)
    private String clave;
    @Column(nullable = false, length = 200)
    private String valor;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
