package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.adapter;

import ec.distribuidoraguayaquil.domain.model.Material;
import ec.distribuidoraguayaquil.domain.port.out.MaterialRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.MaterialEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.MaterialJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MaterialPersistenceAdapter implements MaterialRepositoryPort {

    private final MaterialJpaRepository repository;

    public MaterialPersistenceAdapter(MaterialJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Material> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Material> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Material save(Material material) {
        MaterialEntity e = new MaterialEntity();
        e.setId(material.id());
        e.setName(material.name());
        e.setOptions(material.options() == null ? new ArrayList<>() : new ArrayList<>(material.options()));
        return toDomain(repository.save(e));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private Material toDomain(MaterialEntity e) {
        return new Material(e.getId(), e.getName(), e.getOptions());
    }
}
