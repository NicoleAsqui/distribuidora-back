package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.adapter;

import ec.distribuidoraguayaquil.domain.model.QuoteRequest;
import ec.distribuidoraguayaquil.domain.port.out.QuoteRequestRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.QuoteRequestEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.QuoteRequestJpaRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class QuoteRequestPersistenceAdapter implements QuoteRequestRepositoryPort {

    private final QuoteRequestJpaRepository repository;

    public QuoteRequestPersistenceAdapter(QuoteRequestJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<QuoteRequest> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<QuoteRequest> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public QuoteRequest save(QuoteRequest request) {
        QuoteRequestEntity e = new QuoteRequestEntity();
        e.setId(request.id());
        e.setCode(request.code());
        e.setClient(request.client());
        e.setEmail(request.email());
        e.setPhone(request.phone());
        e.setModelId(request.modelId());
        e.setMaterial(request.material());
        e.setQty(request.qty());
        e.setRequestDate(request.date());
        e.setStatus(request.status());
        e.setNotes(request.notes());
        e.setAttachments(request.attachments() == null ? new ArrayList<>() : new ArrayList<>(request.attachments()));
        return toDomain(repository.save(e));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public long nextSequence() {
        return 1000 + repository.countAll() + 1;
    }

    private QuoteRequest toDomain(QuoteRequestEntity e) {
        return new QuoteRequest(e.getId(), e.getCode(), e.getClient(), e.getEmail(), e.getPhone(),
                e.getModelId(), e.getMaterial(), e.getQty(), e.getRequestDate(), e.getStatus(),
                e.getNotes(), e.getAttachments());
    }
}
