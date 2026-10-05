package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MedidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MedidaRepository extends JpaRepository<MedidaEntity, Long> {
    List<MedidaEntity> findAllByOrderByLargoAscAnchoAscAltoAsc();

    Optional<MedidaEntity> findFirstByLargoAndAnchoAndAltoAndUnidad(
            BigDecimal largo,
            BigDecimal ancho,
            BigDecimal alto,
            String unidad);

    /**
     * Misma caja en planta: 10×25×alto ≡ 25×10×alto (largo y ancho intercambiables).
     */
    @Query("""
            SELECT m FROM MedidaEntity m
            WHERE m.alto = :alto
              AND LOWER(m.unidad) = LOWER(:unidad)
              AND (
                (m.largo = :ladoA AND m.ancho = :ladoB)
                OR (m.largo = :ladoB AND m.ancho = :ladoA)
              )
            ORDER BY m.id ASC
            """)
    List<MedidaEntity> findEquivalentBaseAndAltoAndUnidad(
            @Param("ladoA") BigDecimal ladoA,
            @Param("ladoB") BigDecimal ladoB,
            @Param("alto") BigDecimal alto,
            @Param("unidad") String unidad);
}
