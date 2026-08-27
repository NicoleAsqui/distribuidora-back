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
@Table(name = "disenos")
public class DisenoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    @Column(nullable = false)
    private Integer orden = 0;

    /**
     * Familia del catálogo: acetato | cartulina | mdf | carton.
     * Vive en el diseño; el producto solo la hereda al elegir el modelo.
     */
    @Column(nullable = false, length = 32)
    private String seccion = "cartulina";
}
