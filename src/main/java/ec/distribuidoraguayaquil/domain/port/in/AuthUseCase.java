package ec.distribuidoraguayaquil.domain.port.in;

public interface AuthUseCase {
    AuthResult login(String username, String password);
    boolean validateToken(String token);

    record AuthResult(boolean success, String token, String message) {
    }
}
