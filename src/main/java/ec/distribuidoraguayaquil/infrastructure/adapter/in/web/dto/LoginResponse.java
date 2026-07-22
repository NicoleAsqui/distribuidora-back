package ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto;

public record LoginResponse(boolean success, String token, String message) {
}
