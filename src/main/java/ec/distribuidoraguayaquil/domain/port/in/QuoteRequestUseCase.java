package ec.distribuidoraguayaquil.domain.port.in;

import ec.distribuidoraguayaquil.domain.model.QuoteRequest;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;

import java.util.List;

public interface QuoteRequestUseCase {
    List<QuoteRequest> listAll();
    QuoteRequest getById(String id);
    QuoteRequest create(QuoteRequest request);
    QuoteRequest updateStatus(String id, RequestStatus status);
    QuoteRequest update(String id, QuoteRequest request);
    void delete(String id);
}
