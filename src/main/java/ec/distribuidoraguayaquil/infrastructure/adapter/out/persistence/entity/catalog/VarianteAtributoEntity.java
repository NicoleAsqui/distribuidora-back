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
@Table(name = "variante_atributos")
@IdClass(VarianteAtributoEntity.Key.class)
public class VarianteAtributoEntity {

    @Id
    @Column(name = "variante_id", nullable = false)
    private Long varianteId;

    @Id
    @Column(name = "atributo_valor_id", nullable = false)
    private Long atributoValorId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {
        private Long varianteId;
        private Long atributoValorId;
    }
}
