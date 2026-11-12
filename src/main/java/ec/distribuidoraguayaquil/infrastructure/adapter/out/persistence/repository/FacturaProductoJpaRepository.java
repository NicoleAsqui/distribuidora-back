package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.FacturaProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacturaProductoJpaRepository extends JpaRepository<FacturaProductoEntity, Long> {

    Optional<FacturaProductoEntity> findByClave(String clave);

    Optional<FacturaProductoEntity> findByCodigo(String codigo);

    List<FacturaProductoEntity> findTop10ByClaveContainingOrderByVecesDesc(String texto);
}
