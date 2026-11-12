package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "facturas")
@Getter
@Setter
public class FacturaEntity {
    @Id
    private String id;
    @Column(nullable = false, length = 1)
    private String ambiente;
    @Column(nullable = false)
    private Integer secuencial;
    @Column(nullable = false, length = 20)
    private String numero;
    @Column(name = "clave_acceso", nullable = false, length = 49)
    private String claveAcceso;
    /** CREADA | DEVUELTA | RECIBIDA | EN PROCESO | AUTORIZADO | NO AUTORIZADO | ERROR */
    @Column(nullable = false, length = 30)
    private String estado;
    @Column(name = "fecha_emision", nullable = false, length = 10)
    private String fechaEmision;
    @Column(name = "order_id")
    private String orderId;
    @Column(name = "order_code", length = 40)
    private String orderCode;
    @Column(name = "cliente_identificacion", length = 20)
    private String clienteIdentificacion;
    @Column(name = "cliente_nombre", length = 300)
    private String clienteNombre;
    @Column(name = "cliente_email", length = 200)
    private String clienteEmail;
    @Column(name = "cliente_telefono", length = 40)
    private String clienteTelefono;
    @Column(precision = 14, scale = 2)
    private BigDecimal subtotal;
    @Column(precision = 14, scale = 2)
    private BigDecimal iva;
    @Column(precision = 14, scale = 2)
    private BigDecimal total;
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "datos_json", nullable = false, columnDefinition = "TEXT")
    private String datosJson;
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "xml_firmado", columnDefinition = "TEXT")
    private String xmlFirmado;
    @Column(name = "numero_autorizacion", length = 60)
    private String numeroAutorizacion;
    @Column(name = "fecha_autorizacion", length = 60)
    private String fechaAutorizacion;
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(columnDefinition = "TEXT")
    private String mensajes;
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "ride_url", columnDefinition = "TEXT")
    private String rideUrl;
    @Column(name = "email_enviado_at")
    private Instant emailEnviadoAt;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "created_by", length = 80)
    private String createdBy;
}
