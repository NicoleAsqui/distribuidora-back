package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "material_colores")
@IdClass(MaterialColorEntity.Key.class)
public class MaterialColorEntity {

    @Id
    @Column(name = "material_id", nullable = false)
    private Long materialId;

    @Id
    @Column(name = "color_id", nullable = false)
    private Long colorId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {
        private Long materialId;
        private Long colorId;
    }
}
