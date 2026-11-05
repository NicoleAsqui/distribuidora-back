package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

/** Cuerpos de las acciones de agenda de pedidos. */
public final class OrderAgendaRequests {
    private OrderAgendaRequests() {
    }

    public record Reschedule(@NotNull Instant scheduledAt, String reason) {
    }

    public record Payment(@NotNull BigDecimal amount, String note) {
    }

    public record Event(String type, String message) {
    }

    public record StaffUpsert(String nombre, String pin, Boolean activo) {
    }

    public record WorkerDone(String pin, String note) {
    }

    public record WorkerReschedule(String pin, @NotNull Instant scheduledAt, String reason) {
    }
}
