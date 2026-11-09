package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.domain.port.in.AuthUseCase;
import ec.distribuidoraguayaquil.domain.port.in.OrderUseCase;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.PricingQuoteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.PricingQuoteJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Cotización enviada vence a los 7 días; aceptar una caducada exige nota. */
class QuoteExpiryTest {

    private PricingQuoteEntity quote(String status, Instant created) {
        PricingQuoteEntity q = new PricingQuoteEntity();
        q.setId("q1");
        q.setCode("COT-1");
        q.setSource("admin");
        q.setStatus(status);
        q.setCreatedAt(created);
        q.setItemsJson("[]");
        q.setTotal(BigDecimal.TEN);
        return q;
    }

    private PricingQuoteController controller(PricingQuoteEntity q) {
        PricingQuoteJpaRepository repo = mock(PricingQuoteJpaRepository.class);
        when(repo.findById("q1")).thenReturn(Optional.of(q));
        when(repo.findAllOrderedByCreatedAtDesc()).thenReturn(List.of(q));
        when(repo.save(any(PricingQuoteEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        AuthUseCase auth = mock(AuthUseCase.class);
        when(auth.usernameFromToken(any())).thenReturn("jeymy");
        return new PricingQuoteController(repo, new ObjectMapper(), null, mock(OrderUseCase.class), auth);
    }

    @Test
    void enviadaVencidaPasaACaducada() {
        PricingQuoteEntity q = quote("sent", Instant.now().minus(Duration.ofDays(8)));
        List<Map<String, Object>> list = controller(q).list();
        assertEquals("expired", list.get(0).get("status"));
        assertTrue(String.valueOf(list.get(0).get("validUntil")).length() > 10);
    }

    @Test
    void enviadaVigenteSigueEnviada() {
        PricingQuoteEntity q = quote("sent", Instant.now().minus(Duration.ofDays(2)));
        assertEquals("sent", controller(q).list().get(0).get("status"));
    }

    @Test
    void caducadaAAceptadaExigeNota() {
        PricingQuoteEntity q = quote("sent", Instant.now().minus(Duration.ofDays(10)));
        PricingQuoteController c = controller(q);
        HttpServletRequest http = mock(HttpServletRequest.class);
        assertThrows(ResponseStatusException.class,
                () -> c.updateStatus("q1", new java.util.HashMap<>(Map.of("status", "accepted")), http));
        Map<String, Object> ok = c.updateStatus("q1",
                new java.util.HashMap<>(Map.of("status", "accepted", "note", "cliente pagó en efectivo")), http);
        assertEquals("accepted", ok.get("status"));
        assertTrue(String.valueOf(ok.get("notes")).contains("Aceptada fuera de plazo por Jeymy: cliente pagó en efectivo"));
    }
}
