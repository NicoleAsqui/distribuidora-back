package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto;

import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(@NotNull RequestStatus status) {
}
