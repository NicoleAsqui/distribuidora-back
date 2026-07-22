package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.adapter;

import ec.distribuidoraguayaquil.domain.model.Color;
import ec.distribuidoraguayaquil.domain.port.out.ColorRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.ColorEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.ColorJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ColorPersistenceAdapter implements ColorRepositoryPort {

    private final ColorJpaRepository repository;

    public ColorPersistenceAdapter(ColorJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Color> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Color> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Color save(Color color) {
        ColorEntity e = new ColorEntity();
        e.setId(color.id());
        e.setName(color.name());
        e.setHex(color.hex());
        e.setAvailable(color.available());
        return toDomain(repository.save(e));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private Color toDomain(ColorEntity e) {
        return new Color(e.getId(), e.getName(), e.getHex(), e.isAvailable());
    }
}
