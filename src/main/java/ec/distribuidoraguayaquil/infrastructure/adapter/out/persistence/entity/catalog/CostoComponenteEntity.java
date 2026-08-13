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
@Table(name = "costos_componentes")
public class CostoComponenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variante_componente_id", nullable = false, unique = true)
    private Long varianteComponenteId;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal costo;
}
