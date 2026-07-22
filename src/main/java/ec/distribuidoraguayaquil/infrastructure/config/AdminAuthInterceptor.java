package ec.distribuidoraguayaquil.infrastructure.config;

import ec.distribuidoraguayaquil.domain.port.in.AuthUseCase;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final AuthUseCase authUseCase;

    public AdminAuthInterceptor(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (!requiresAuth(path, method)) {
            return true;
        }

        String token = extractToken(request);
        if (!authUseCase.validateToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        return true;
    }

    private boolean requiresAuth(String path, String method) {
        if (path.startsWith("/api/admin/")) {
            return true;
        }
        if (path.startsWith("/api/products/admin")) {
            return true;
        }
        if (path.equals("/api/products") && !HttpMethod.GET.matches(method)) {
            return true;
        }
        if (path.matches("/api/products/[^/]+") && (HttpMethod.PUT.matches(method) || HttpMethod.DELETE.matches(method))) {
            return true;
        }
        if (path.startsWith("/api/quotes")) {
            // público: crear cotización; admin: listar/editar
            return !HttpMethod.POST.matches(method) || path.contains("/status");
        }
        if (path.startsWith("/api/orders")) {
            // público: crear pedido; admin: listar/editar
            if (HttpMethod.POST.matches(method) && path.equals("/api/orders")) {
                return false;
            }
            return true;
        }
        if (path.equals("/api/config") && HttpMethod.PUT.matches(method)) {
            return true;
        }
        return false;
    }

    private String extractToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return request.getHeader("X-Admin-Token");
    }
}
