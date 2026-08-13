package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "viniles")
public class VinilEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** mate | brillante | impreso */
    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(name = "codigo_hex")
    private String codigoHex;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "imagen_thumb_url")
    private String imagenThumbUrl;

    @Column(name = "requiere_arte", nullable = false)
    private Boolean requiereArte = Boolean.FALSE;

    @Column(name = "precio_ref")
    private BigDecimal precioRef;

    @Column(name = "unidad_precio")
    private String unidadPrecio;

    @Column(nullable = false)
    private Boolean activo = Boolean.TRUE;

    @Column(nullable = false)
    private Integer orden = 0;
}
