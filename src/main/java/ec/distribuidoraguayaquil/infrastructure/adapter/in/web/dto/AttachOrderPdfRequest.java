package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record AttachOrderPdfRequest(
        @NotBlank String pdfUrl
) {
}
