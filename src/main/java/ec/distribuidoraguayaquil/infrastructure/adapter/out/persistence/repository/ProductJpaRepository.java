package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findByRef(String ref);
    List<ProductEntity> findByActiveTrue();
    List<ProductEntity> findByActiveTrueAndTopProductTrue();
}
