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

    /** Cómo se vende: unidad | paquete | carton | caja */
    @Column(name = "unidad_venta", nullable = false)
    private String unidadVenta = "unidad";

    /** Unidades sueltas dentro del empaque (ej. 20 por paquete). Null si es unidad. */
    @Column(name = "unidades_contenido")
    private Integer unidadesContenido;

    /** Si true, aparece en «Cajas más vendidas» (home / listado TOP). */
    @Column(nullable = false)
    private Boolean destacado = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;
}
