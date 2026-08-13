package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VarianteRepository extends JpaRepository<VarianteEntity, Long> {
    Optional<VarianteEntity> findBySku(String sku);
    List<VarianteEntity> findByActivoTrue();
    List<VarianteEntity> findByDisenoIdOrderByIdAsc(Long disenoId);
    List<VarianteEntity> findByDisenoIdAndActivoTrueOrderByIdAsc(Long disenoId);

    long countByActivoTrue();

    @Query("""
            SELECT v.disenoId, COUNT(v.id) FROM VarianteEntity v
            WHERE v.activo = TRUE
            GROUP BY v.disenoId
            """)
    List<Object[]> countActiveGroupedByDisenoId();

    @Query("""
            SELECT v FROM VarianteEntity v, DisenoEntity d
            WHERE v.disenoId = d.id
              AND (:includeInactive = TRUE OR v.activo = TRUE)
              AND (:disenoId IS NULL OR v.disenoId = :disenoId)
              AND (
                :qBlank = TRUE
                OR LOWER(v.sku) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(d.nombre, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(d.slug, '')) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            ORDER BY COALESCE(d.orden, 2147483647) ASC, v.id ASC
            """)
    Page<VarianteEntity> pageByFilters(
            @Param("includeInactive") boolean includeInactive,
            @Param("disenoId") Long disenoId,
            @Param("q") String q,
            @Param("qBlank") boolean qBlank,
            Pageable pageable);

    @Query("""
            SELECT v FROM VarianteEntity v
            WHERE v.id IN :ids
              AND (:includeInactive = TRUE OR v.activo = TRUE)
              AND (:disenoId IS NULL OR v.disenoId = :disenoId)
            """)
    List<VarianteEntity> findByIdInFiltered(
            @Param("ids") Collection<Long> ids,
            @Param("includeInactive") boolean includeInactive,
            @Param("disenoId") Long disenoId);

    @Query("""
            SELECT v.id FROM VarianteEntity v, DisenoEntity d
            WHERE v.disenoId = d.id
              AND v.id IN :ids
              AND (:includeInactive = TRUE OR v.activo = TRUE)
              AND (:disenoId IS NULL OR v.disenoId = :disenoId)
              AND (
                :qBlank = TRUE
                OR LOWER(v.sku) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(d.nombre, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(d.slug, '')) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    List<Long> filterIdsByQuery(
            @Param("ids") Collection<Long> ids,
            @Param("includeInactive") boolean includeInactive,
            @Param("disenoId") Long disenoId,
            @Param("q") String q,
            @Param("qBlank") boolean qBlank);
}
