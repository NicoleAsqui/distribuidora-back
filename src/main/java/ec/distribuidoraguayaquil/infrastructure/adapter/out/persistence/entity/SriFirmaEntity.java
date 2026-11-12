package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "sri_firma")
@Getter
@Setter
public class SriFirmaEntity {
    @Id
    private Integer id;
    /** .p12 cifrado con AES-GCM (además de su propia clave). */
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "p12_cifrado", nullable = false, columnDefinition = "TEXT")
    private String p12Cifrado;
    @Column(length = 300)
    private String titular;
    @Column(name = "valida_desde")
    private Instant validaDesde;
    @Column(name = "valida_hasta")
    private Instant validaHasta;
    @Column(name = "subida_at", nullable = false)
    private Instant subidaAt;
    @Column(name = "subida_por", length = 80)
    private String subidaPor;
}
