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
@Table(name = "guias")
public class GuiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** informacion | tutoriales */
    @Column(nullable = false, length = 32)
    private String seccion;

    @Column(nullable = false, length = 120)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String resumen;

    @Column(length = 80)
    private String etiqueta;

    @Column(columnDefinition = "TEXT")
    private String cuerpo;

    /** Enlace de YouTube (watch, youtu.be o Shorts) para embeber en el artículo. */
    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    @Column(nullable = false)
    private Integer orden = 0;
}
