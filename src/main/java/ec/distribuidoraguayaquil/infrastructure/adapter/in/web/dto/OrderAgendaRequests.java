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

    /** Datos de la guía de envío (todos opcionales; solo se cambia lo que venga). */
    public record Shipping(String firstName, String lastName, String cedula, Integer packages, String province,
                           String city, String note, String phone, String email) {
    }

    public record Delivery(String fulfillmentMethod, String city, String address, String references, String mapsUrl,
                           String note) {
    }

    public record Event(String type, String message) {
    }

    public record StaffUpsert(String nombre, String pin, Boolean activo) {
    }

    /** collectBalance = el trabajador confirma que cobró el saldo pendiente. */
    public record WorkerDone(String pin, String note, Boolean collectBalance) {
    }

    public record WorkerPayment(String pin, @NotNull BigDecimal amount, String note) {
    }

    public record WorkerReschedule(String pin, @NotNull Instant scheduledAt, String reason) {
    }
}
