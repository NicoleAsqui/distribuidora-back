package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Muestra de color/textura (cartulina blanca, kraft, etc.). */
@Getter
@Setter
@Entity
@Table(name = "texturas")
public class TexturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "imagen_thumb_url")
    private String imagenThumbUrl;

    @Column(nullable = false, length = 32)
    private String seccion = "cartulina";

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    @Column(nullable = false)
    private Integer orden = 0;
}
