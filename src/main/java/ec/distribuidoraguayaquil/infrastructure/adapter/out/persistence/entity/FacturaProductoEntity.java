package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/** Producto facturado fuera del catálogo, con código único reutilizable (FX-0001…). */
@Entity
@Table(name = "factura_productos")
@Getter
@Setter
public class FacturaProductoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 25, unique = true)
    private String codigo;
    /** Descripción normalizada (sin tildes, mayúsculas) para reconocer el mismo producto. */
    @Column(nullable = false, length = 300, unique = true)
    private String clave;
    @Column(nullable = false, length = 300)
    private String descripcion;
    @Column(name = "ultimo_precio", precision = 14, scale = 6)
    private BigDecimal ultimoPrecio;
    @Column(nullable = false)
    private Integer veces;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
