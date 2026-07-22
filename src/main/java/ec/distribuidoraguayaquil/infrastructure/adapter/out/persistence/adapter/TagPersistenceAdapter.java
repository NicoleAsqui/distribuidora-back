package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.adapter;

import ec.distribuidoraguayaquil.domain.model.Tag;
import ec.distribuidoraguayaquil.domain.port.out.TagRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.TagEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.TagJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TagPersistenceAdapter implements TagRepositoryPort {

    private final TagJpaRepository repository;

    public TagPersistenceAdapter(TagJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Tag> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Tag> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Tag save(Tag tag) {
        TagEntity e = new TagEntity();
        e.setId(tag.id());
        e.setName(tag.name());
        e.setColor(tag.color());
        return toDomain(repository.save(e));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private Tag toDomain(TagEntity e) {
        return new Tag(e.getId(), e.getName(), e.getColor());
    }
}
