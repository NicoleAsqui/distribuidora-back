package ec.distribuidoraguayaquil.domain.model;

import java.time.LocalDate;
import java.util.List;

public record QuoteRequest(
        String id,
        String code,
        String client,
        String email,
        String phone,
        String modelId,
        String material,
        int qty,
        LocalDate date,
        RequestStatus status,
        String notes,
        List<String> attachments
) {
}
