package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "variantes")
public class VarianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diseno_id", nullable = false)
    private Long disenoId;

    @Column(name = "medida_id", nullable = false)
    private Long medidaId;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;
}
