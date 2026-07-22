package ec.distribuidoraguayaquil.domain.port.out;

import ec.distribuidoraguayaquil.domain.model.Material;

import java.util.List;
import java.util.Optional;

public interface MaterialRepositoryPort {
    List<Material> findAll();
    Optional<Material> findById(String id);
    Material save(Material material);
    void deleteById(String id);
}
