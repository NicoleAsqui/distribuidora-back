package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto;

import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record QuoteRequestDto(
        String id,
        String code,
        @NotBlank String client,
        String email,
        String phone,
        String modelId,
        String material,
        @Min(1) int qty,
        LocalDate date,
        RequestStatus status,
        String notes,
        List<String> attachments
) {
}
