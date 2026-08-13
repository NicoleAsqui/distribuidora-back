package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.util.List;

public record EmitirFacturaAdminRequest(
        @NotBlank
        @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "fechaEmision debe ser dd/mm/yyyy")
        String fechaEmision,
        @NotNull @Valid CompradorDto comprador,
        @NotEmpty @Valid List<DetalleLineaDto> detalles,
        /** Forma de pago SRI, default 01. */
        String formaPago
) {
    public record CompradorDto(
            @NotBlank String tipoIdentificacion,
            @NotBlank String identificacion,
            @NotBlank String razonSocial,
            String direccion,
            String telefono,
            String email
    ) {
    }

    public record DetalleLineaDto(
            @NotBlank String codigoPrincipal,
            @NotBlank String descripcion,
            @NotNull @Min(0) BigDecimal cantidad,
            @NotNull @Min(0) BigDecimal precioUnitario,
            BigDecimal descuento
    ) {
    }
}
