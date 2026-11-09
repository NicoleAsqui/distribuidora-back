package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto;

import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Cambio de estado. Para PAID: scheduledAt (fecha/hora de entrega) y opcionalmente el abono inicial.
 */
public record StatusUpdateRequest(
        @NotNull RequestStatus status,
        Instant scheduledAt,
        BigDecimal deposit,
        String note,
        /** Al pasar a Pagado: cédula de quien recibe (para la guía). */
        String cedula,
        /** Al pasar a Pagado: número de paquetes (para la guía). */
        Integer packages
) {
}
