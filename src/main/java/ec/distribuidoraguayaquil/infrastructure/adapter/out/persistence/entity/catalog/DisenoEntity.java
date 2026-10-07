package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "disenos",
        uniqueConstraints = {
                @UniqueConstraint(name = "disenos_seccion_nombre_uidx", columnNames = {"seccion", "nombre"})
        }
)
public class DisenoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Único por sección (material): puede repetirse en cartulina vs mdf. */
    @Column(nullable = false)
    private String nombre;

    /** Único global (URLs del catálogo público). */
    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    @Column(nullable = false)
    private Integer orden = 0;

    /**
     * Familia del catálogo: acetato | cartulina | mdf | carton | tarjetas | varios.
     * Vive en el diseño; el producto solo la hereda al elegir el modelo.
     */
    @Column(nullable = false, length = 32)
    private String seccion = "cartulina";

    /**
     * Id del motor de cotización (quote-engine), p.ej. cartulina_tapa, mdf_hexagono.
     * Define la fórmula de precios/cortes de este diseño.
     */
    @Column(nullable = false, length = 64)
    private String motor = "cartulina_tapa";

    /** Foto del modelo en catálogo (full, GCS). */
    @Column(name = "imagen_url")
    private String imagenUrl;

    /** Miniatura para grillas de modelos. */
    @Column(name = "imagen_thumb_url")
    private String imagenThumbUrl;

    /** Reel / video Instagram del modelo (permalink). Opcional. */
    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;
}
