package ec.distribuidoraguayaquil.domain.port.out;

import ec.distribuidoraguayaquil.domain.model.Finish;

import java.util.List;
import java.util.Optional;

public interface FinishRepositoryPort {
    List<Finish> findAll();
    Optional<Finish> findById(String id);
    Finish save(Finish finish);
    void deleteById(String id);
}
