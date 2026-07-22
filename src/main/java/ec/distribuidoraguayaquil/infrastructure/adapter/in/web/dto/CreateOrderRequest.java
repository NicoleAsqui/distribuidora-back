package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank String clientName,
        String clientEmail,
        String clientPhone,
        String notes,
        @NotEmpty @Valid List<OrderItemDto> items
) {
    public record OrderItemDto(
            @NotBlank String productRef,
            @NotBlank String name,
            String size,
            String color,
            @NotNull BigDecimal price,
            @Min(1) int qty
    ) {
    }
}
