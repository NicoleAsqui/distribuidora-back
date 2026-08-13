package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "configuracion_interior_detalles")
public class ConfiguracionInteriorDetalleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "configuracion_id", nullable = false)
    private Long configuracionId;

    private Integer cantidad;

    @Column(precision = 10, scale = 2)
    private BigDecimal alto;

    @Column(precision = 10, scale = 2)
    private BigDecimal diametro;

    @Column(columnDefinition = "TEXT")
    private String descripcion;
}
