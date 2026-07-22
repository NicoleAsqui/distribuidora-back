package ec.distribuidoraguayaquil.domain.port.out;

import ec.distribuidoraguayaquil.domain.model.Color;

import java.util.List;
import java.util.Optional;

public interface ColorRepositoryPort {
    List<Color> findAll();
    Optional<Color> findById(String id);
    Color save(Color color);
    void deleteById(String id);
}
