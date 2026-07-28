package ec.distribuidoraguayaquil.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.mail")
    public MailProperties mailProperties() {
        return new MailProperties();
    }

    public static class MailProperties {
        private String resendApiKey = "";
        private String from = "noreply@distribuidoraguayaquil.com";
        private String fromName = "Distribuidora Guayaquil";

        public String getResendApiKey() { return resendApiKey; }
        public void setResendApiKey(String resendApiKey) { this.resendApiKey = resendApiKey; }
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getFromName() { return fromName; }
        public void setFromName(String fromName) { this.fromName = fromName; }

        public boolean isConfigured() {
            return resendApiKey != null && !resendApiKey.isBlank();
        }

        public String fromHeader() {
            String email = from == null || from.isBlank() ? "noreply@distribuidoraguayaquil.com" : from;
            String name = fromName == null || fromName.isBlank() ? "Distribuidora Guayaquil" : fromName;
            return name + " <" + email + ">";
        }
    }
}
