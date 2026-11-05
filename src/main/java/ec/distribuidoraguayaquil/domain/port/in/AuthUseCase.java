package ec.distribuidoraguayaquil.domain.port.in;

public interface AuthUseCase {
    AuthResult login(String username, String password);
    boolean validateToken(String token);

    /** Usuario dueño de un token válido (para firmar el historial), o null. */
    String usernameFromToken(String token);

    record AuthResult(boolean success, String token, String message) {
    }
}
