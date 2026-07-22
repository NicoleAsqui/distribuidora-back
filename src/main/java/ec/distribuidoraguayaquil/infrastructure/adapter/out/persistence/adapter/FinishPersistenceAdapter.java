package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.adapter;

import ec.distribuidoraguayaquil.domain.model.Finish;
import ec.distribuidoraguayaquil.domain.port.out.FinishRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.FinishEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.FinishJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FinishPersistenceAdapter implements FinishRepositoryPort {

    private final FinishJpaRepository repository;

    public FinishPersistenceAdapter(FinishJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Finish> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Finish> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Finish save(Finish finish) {
        FinishEntity e = new FinishEntity();
        e.setId(finish.id());
        e.setName(finish.name());
        e.setDescription(finish.description());
        return toDomain(repository.save(e));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private Finish toDomain(FinishEntity e) {
        return new Finish(e.getId(), e.getName(), e.getDescription());
    }
}
