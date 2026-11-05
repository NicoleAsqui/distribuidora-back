package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.AppSecretEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSecretJpaRepository extends JpaRepository<AppSecretEntity, String> {
}
