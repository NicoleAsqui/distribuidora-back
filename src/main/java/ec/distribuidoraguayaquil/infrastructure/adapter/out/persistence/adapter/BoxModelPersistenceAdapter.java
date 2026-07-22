package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.adapter;

import ec.distribuidoraguayaquil.domain.model.BoxModel;
import ec.distribuidoraguayaquil.domain.port.out.BoxModelRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.BoxModelEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.BoxModelJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BoxModelPersistenceAdapter implements BoxModelRepositoryPort {

    private final BoxModelJpaRepository repository;

    public BoxModelPersistenceAdapter(BoxModelJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BoxModel> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<BoxModel> findActive() {
        return repository.findByActiveTrue().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<BoxModel> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public BoxModel save(BoxModel model) {
        BoxModelEntity e = new BoxModelEntity();
        e.setId(model.id());
        e.setName(model.name());
        e.setCategoryId(model.categoryId());
        e.setDescription(model.description());
        e.setPhotos(copy(model.photos()));
        e.setMaterials(copy(model.materials()));
        e.setFinishes(copy(model.finishes()));
        e.setColors(copy(model.colors()));
        e.setMinQty(model.minQty());
        e.setLeadDays(model.leadDays());
        e.setTags(copy(model.tags()));
        e.setActive(model.active());
        return toDomain(repository.save(e));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private BoxModel toDomain(BoxModelEntity e) {
        return new BoxModel(e.getId(), e.getName(), e.getCategoryId(), e.getDescription(),
                e.getPhotos(), e.getMaterials(), e.getFinishes(), e.getColors(),
                e.getMinQty(), e.getLeadDays(), e.getTags(), e.isActive());
    }

    private static List<String> copy(List<String> values) {
        return values == null ? new ArrayList<>() : new ArrayList<>(values);
    }
}
