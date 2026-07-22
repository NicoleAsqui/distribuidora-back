package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<TagEntity, String> {
}
