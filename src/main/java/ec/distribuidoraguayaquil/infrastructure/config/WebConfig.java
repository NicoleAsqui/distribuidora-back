package ec.distribuidoraguayaquil.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final String corsOrigins;

    public WebConfig(
            AdminAuthInterceptor adminAuthInterceptor,
            @Value("${app.cors.allowed-origins:*}") String corsOrigins) {
        this.adminAuthInterceptor = adminAuthInterceptor;
        this.corsOrigins = corsOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = corsOrigins.split(",");
        var registration = registry.addMapping("/api/**")
                .allowedOriginPatterns(origins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization");
        if (!"*".equals(corsOrigins.trim()) && !corsOrigins.contains("*")) {
            registration.allowCredentials(true);
        }
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor).addPathPatterns("/api/**");
    }
}
