package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.QuoteEngineConfigEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.QuoteEngineConfigJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
@RequestMapping("/api/quote-engine")
public class QuoteEngineController {

    private final QuoteEngineConfigJpaRepository repository;
    private final ObjectMapper objectMapper;

    public QuoteEngineController(QuoteEngineConfigJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{motor}")
    public JsonNode get(@PathVariable String motor) {
        return repository.findById(motor)
                .map(this::parse)
                .orElseGet(objectMapper::createObjectNode);
    }

    @PutMapping("/{motor}")
    public JsonNode save(@PathVariable String motor, @RequestBody Map<String, Object> body) {
        try {
            QuoteEngineConfigEntity entity = repository.findById(motor).orElseGet(QuoteEngineConfigEntity::new);
            entity.setMotor(motor);
            entity.setConfigJson(objectMapper.writeValueAsString(body));
            repository.save(entity);
            return parse(entity);
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "JSON inválido");
        }
    }

    private JsonNode parse(QuoteEngineConfigEntity entity) {
        try {
            return objectMapper.readTree(entity.getConfigJson());
        } catch (JacksonException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Config inválida");
        }
    }
}
