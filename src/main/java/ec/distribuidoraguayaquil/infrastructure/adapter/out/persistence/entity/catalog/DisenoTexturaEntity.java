package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Foto del modelo asociada a un color/textura (vive en el diseño, no en cada SKU). */
@Getter
@Setter
@Entity
@Table(name = "diseno_texturas")
public class DisenoTexturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diseno_id", nullable = false)
    private Long disenoId;

    @Column(name = "textura_id", nullable = false)
    private Long texturaId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "url_thumb", columnDefinition = "TEXT")
    private String urlThumb;

    @Column(nullable = false)
    private Integer orden = 0;
}
