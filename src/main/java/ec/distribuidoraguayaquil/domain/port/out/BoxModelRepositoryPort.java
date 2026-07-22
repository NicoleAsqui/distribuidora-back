package ec.distribuidoraguayaquil.domain.port.out;

import ec.distribuidoraguayaquil.domain.model.BoxModel;

import java.util.List;
import java.util.Optional;

public interface BoxModelRepositoryPort {
    List<BoxModel> findAll();
    List<BoxModel> findActive();
    Optional<BoxModel> findById(String id);
    BoxModel save(BoxModel model);
    void deleteById(String id);
}
