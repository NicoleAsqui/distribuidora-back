package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.port.in.AuthUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class AuthService implements AuthUseCase {

    private final String adminUser;
    private final String adminPassword;
    private final String tokenSecret;
    private final long tokenTtlSeconds;

    public AuthService(
            @Value("${app.admin.username:admin}") String adminUser,
            @Value("${app.admin.password:admin123}") String adminPassword,
            @Value("${app.admin.token-secret:distribuidora-dev-secret-change-me}") String tokenSecret,
            @Value("${app.admin.token-ttl-seconds:86400}") long tokenTtlSeconds) {
        this.adminUser = adminUser;
        this.adminPassword = adminPassword;
        this.tokenSecret = tokenSecret;
        this.tokenTtlSeconds = tokenTtlSeconds;
    }

    @Override
    public AuthResult login(String username, String password) {
        if (adminUser.equals(username) && adminPassword.equals(password)) {
            long exp = Instant.now().getEpochSecond() + tokenTtlSeconds;
            String payload = username + ":" + exp;
            String sig = hmac(payload);
            String token = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString((payload + ":" + sig).getBytes(StandardCharsets.UTF_8));
            return new AuthResult(true, token, "OK");
        }
        return new AuthResult(false, null, "Credenciales inválidas");
    }

    @Override
    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            String raw = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = raw.split(":");
            if (parts.length != 3) {
                return false;
            }
            String username = parts[0];
            long exp = Long.parseLong(parts[1]);
            String sig = parts[2];
            if (!adminUser.equals(username) || Instant.now().getEpochSecond() > exp) {
                return false;
            }
            return hmac(username + ":" + exp).equals(sig);
        } catch (Exception e) {
            return false;
        }
    }

    private String hmac(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(tokenSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token", e);
        }
    }
}
