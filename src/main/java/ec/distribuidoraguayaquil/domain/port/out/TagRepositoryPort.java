package ec.distribuidoraguayaquil.domain.port.out;

import ec.distribuidoraguayaquil.domain.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagRepositoryPort {
    List<Tag> findAll();
    Optional<Tag> findById(String id);
    Tag save(Tag tag);
    void deleteById(String id);
}
