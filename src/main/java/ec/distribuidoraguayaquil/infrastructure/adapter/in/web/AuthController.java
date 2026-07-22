package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.domain.port.in.AuthUseCase;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.LoginRequest;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        AuthUseCase.AuthResult result = authUseCase.login(request.username(), request.password());
        if (!result.success()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, result.message());
        }
        return new LoginResponse(true, result.token(), result.message());
    }
}
