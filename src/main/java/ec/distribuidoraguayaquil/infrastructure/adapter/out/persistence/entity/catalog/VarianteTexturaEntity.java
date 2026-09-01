package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Foto del producto asociada a un color/textura concreto. */
@Getter
@Setter
@Entity
@Table(name = "variante_texturas")
public class VarianteTexturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variante_id", nullable = false)
    private Long varianteId;

    @Column(name = "textura_id", nullable = false)
    private Long texturaId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "url_thumb", columnDefinition = "TEXT")
    private String urlThumb;

    @Column(nullable = false)
    private Integer orden = 0;
}
