package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.domain.model.QuoteRequest;
import ec.distribuidoraguayaquil.domain.port.in.QuoteRequestUseCase;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.QuoteRequestDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.StatusUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quotes")
public class QuoteRequestController {

    private final QuoteRequestUseCase quoteRequestUseCase;

    public QuoteRequestController(QuoteRequestUseCase quoteRequestUseCase) {
        this.quoteRequestUseCase = quoteRequestUseCase;
    }

    @GetMapping
    public List<QuoteRequest> list() {
        return quoteRequestUseCase.listAll();
    }

    @GetMapping("/{id}")
    public QuoteRequest get(@PathVariable String id) {
        return quoteRequestUseCase.getById(id);
    }

    @PostMapping
    public QuoteRequest create(@Valid @RequestBody QuoteRequestDto dto) {
        return quoteRequestUseCase.create(new QuoteRequest(
                dto.id(), dto.code(), dto.client(), dto.email(), dto.phone(), dto.modelId(),
                dto.material(), dto.qty(), dto.date(), dto.status(), dto.notes(), dto.attachments()));
    }

    @PutMapping("/{id}")
    public QuoteRequest update(@PathVariable String id, @Valid @RequestBody QuoteRequestDto dto) {
        return quoteRequestUseCase.update(id, new QuoteRequest(
                id, dto.code(), dto.client(), dto.email(), dto.phone(), dto.modelId(),
                dto.material(), dto.qty(), dto.date(), dto.status(), dto.notes(), dto.attachments()));
    }

    @PutMapping("/{id}/status")
    public QuoteRequest updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        return quoteRequestUseCase.updateStatus(id, request.status());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        quoteRequestUseCase.delete(id);
    }
}
