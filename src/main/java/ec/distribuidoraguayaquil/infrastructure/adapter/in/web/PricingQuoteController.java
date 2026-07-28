package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.PricingQuoteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.PricingQuoteJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pricing-quotes")
public class PricingQuoteController {

    private final PricingQuoteJpaRepository repository;
    private final ObjectMapper objectMapper;

    public PricingQuoteController(PricingQuoteJpaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<Map<String, Object>> list() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(this::toMap).toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable String id) {
        return repository.findById(id).map(this::toMap)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Map<String, Object> body) {
        return toMap(repository.save(fromBody(body, true)));
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        PricingQuoteEntity existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        body.put("id", id);
        body.putIfAbsent("code", existing.getCode());
        body.putIfAbsent("createdAt", existing.getCreatedAt().toString());
        return toMap(repository.save(fromBody(body, false)));
    }

    @PutMapping("/{id}/status")
    public Map<String, Object> updateStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        PricingQuoteEntity e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        e.setStatus(body.getOrDefault("status", e.getStatus()));
        return toMap(repository.save(e));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        repository.deleteById(id);
    }

    private PricingQuoteEntity fromBody(Map<String, Object> body, boolean isNew) {
        PricingQuoteEntity e = new PricingQuoteEntity();
        String id = body.get("id") == null || String.valueOf(body.get("id")).isBlank()
                ? UUID.randomUUID().toString()
                : String.valueOf(body.get("id"));
        e.setId(id);
        String code = body.get("code") == null || String.valueOf(body.get("code")).isBlank()
                ? "COT-" + System.currentTimeMillis() % 1_000_000
                : String.valueOf(body.get("code"));
        e.setCode(code);
        e.setSource(String.valueOf(body.getOrDefault("source", "web")));
        if (isNew || body.get("createdAt") == null) {
            e.setCreatedAt(Instant.now());
        } else {
            e.setCreatedAt(Instant.parse(String.valueOf(body.get("createdAt"))));
        }
        e.setClientName(asString(body.get("clientName")));
        e.setClientPhone(asString(body.get("clientPhone")));
        e.setClientEmail(asString(body.get("clientEmail")));
        e.setDeliveryDate(asString(body.get("deliveryDate")));
        e.setStatus(String.valueOf(body.getOrDefault("status", "sent")));
        e.setNotes(asString(body.get("notes")));
        Object total = body.get("total");
        e.setTotal(total == null ? BigDecimal.ZERO : new BigDecimal(String.valueOf(total)));
        try {
            Object items = body.get("items");
            e.setItemsJson(objectMapper.writeValueAsString(items == null ? List.of() : items));
        } catch (JacksonException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "items inválidos");
        }
        return e;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMap(PricingQuoteEntity e) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", e.getId());
        map.put("code", e.getCode());
        map.put("source", e.getSource());
        map.put("createdAt", e.getCreatedAt().toString());
        map.put("clientName", e.getClientName());
        map.put("clientPhone", e.getClientPhone());
        map.put("clientEmail", e.getClientEmail());
        map.put("deliveryDate", e.getDeliveryDate());
        map.put("status", e.getStatus());
        map.put("notes", e.getNotes());
        map.put("total", e.getTotal());
        try {
            map.put("items", objectMapper.readValue(e.getItemsJson(), List.class));
        } catch (JacksonException ex) {
            map.put("items", List.of());
        }
        return map;
    }

    private static String asString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
