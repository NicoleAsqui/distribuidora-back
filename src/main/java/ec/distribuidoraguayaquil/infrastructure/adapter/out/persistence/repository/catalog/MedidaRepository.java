package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MedidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
