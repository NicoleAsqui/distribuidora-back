package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "sri_ajustes")
@Getter
@Setter
public class SriAjustesEntity {
    @Id
    private Integer id;
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "datos_json", nullable = false, columnDefinition = "TEXT")
    private String datosJson;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Column(name = "updated_by", length = 80)
    private String updatedBy;
}
