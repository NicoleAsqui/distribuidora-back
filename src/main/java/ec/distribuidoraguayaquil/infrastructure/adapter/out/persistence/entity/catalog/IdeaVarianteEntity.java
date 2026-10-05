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
@Table(name = "idea_variantes")
public class IdeaVarianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idea_id", nullable = false)
    private Long ideaId;

    @Column(name = "variante_id", nullable = false)
    private Long varianteId;

    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Foto full de esta idea individual (lo que ve el cliente). */
    @Column(columnDefinition = "TEXT")
    private String url;

    /** Miniatura de la foto de la idea individual. */
    @Column(name = "url_thumb", columnDefinition = "TEXT")
    private String urlThumb;

    @Column(nullable = false)
    private Integer orden = 0;
}
