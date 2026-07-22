package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.SiteConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteConfigJpaRepository extends JpaRepository<SiteConfigEntity, String> {
}
