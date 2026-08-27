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
@Table(name = "diseno_imagenes")
public class DisenoImagenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "diseno_id", nullable = false)
    private Long disenoId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "url_thumb", columnDefinition = "TEXT")
    private String urlThumb;

    @Column(nullable = false)
    private Boolean principal = Boolean.FALSE;

    @Column(nullable = false)
    private Integer orden = 0;
}
