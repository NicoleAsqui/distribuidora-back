package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MedidaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedidaRepository extends JpaRepository<MedidaEntity, Long> {
    List<MedidaEntity> findAllByOrderByLargoAscAnchoAscAltoAsc();
}
