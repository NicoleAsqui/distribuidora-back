package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.StaffMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffMemberJpaRepository extends JpaRepository<StaffMemberEntity, Long> {
    List<StaffMemberEntity> findAllByOrderByNombreAsc();

    Optional<StaffMemberEntity> findByPinHashAndActivoTrue(String pinHash);

    Optional<StaffMemberEntity> findByPinHash(String pinHash);

    boolean existsByNombreIgnoreCase(String nombre);
}
