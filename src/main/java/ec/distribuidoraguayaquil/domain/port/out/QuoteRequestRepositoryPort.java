package ec.distribuidoraguayaquil.domain.port.out;

import ec.distribuidoraguayaquil.domain.model.QuoteRequest;

import java.util.List;
import java.util.Optional;

public interface QuoteRequestRepositoryPort {
    List<QuoteRequest> findAll();
    Optional<QuoteRequest> findById(String id);
    QuoteRequest save(QuoteRequest request);
    void deleteById(String id);
    long nextSequence();
}
