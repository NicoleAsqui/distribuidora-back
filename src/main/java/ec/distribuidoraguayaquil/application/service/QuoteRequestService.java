package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.model.QuoteRequest;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import ec.distribuidoraguayaquil.domain.port.in.QuoteRequestUseCase;
import ec.distribuidoraguayaquil.domain.port.out.QuoteRequestRepositoryPort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class QuoteRequestService implements QuoteRequestUseCase {

    private final QuoteRequestRepositoryPort repository;

    public QuoteRequestService(QuoteRequestRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public List<QuoteRequest> listAll() {
        return repository.findAll();
    }

    @Override
    public QuoteRequest getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cotización no encontrada"));
    }

    @Override
    public QuoteRequest create(QuoteRequest request) {
        long seq = repository.nextSequence();
        String code = request.code() == null || request.code().isBlank()
                ? "COT-" + seq
                : request.code();
        QuoteRequest toSave = new QuoteRequest(
                UUID.randomUUID().toString(),
                code,
                request.client(),
                request.email(),
                request.phone(),
                request.modelId(),
                request.material(),
                request.qty(),
                request.date() == null ? LocalDate.now() : request.date(),
                request.status() == null ? RequestStatus.PENDING : request.status(),
                request.notes(),
                request.attachments() == null ? List.of() : request.attachments()
        );
        return repository.save(toSave);
    }

    @Override
    public QuoteRequest updateStatus(String id, RequestStatus status) {
        QuoteRequest existing = getById(id);
        return repository.save(new QuoteRequest(
                existing.id(), existing.code(), existing.client(), existing.email(), existing.phone(),
                existing.modelId(), existing.material(), existing.qty(), existing.date(),
                status, existing.notes(), existing.attachments()));
    }

    @Override
    public QuoteRequest update(String id, QuoteRequest request) {
        QuoteRequest existing = getById(id);
        return repository.save(new QuoteRequest(
                existing.id(),
                existing.code(),
                request.client(),
                request.email(),
                request.phone(),
                request.modelId(),
                request.material(),
                request.qty(),
                request.date() == null ? existing.date() : request.date(),
                request.status() == null ? existing.status() : request.status(),
                request.notes(),
                request.attachments() == null ? existing.attachments() : request.attachments()
        ));
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}
