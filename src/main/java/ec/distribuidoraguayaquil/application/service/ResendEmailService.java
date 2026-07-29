package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.infrastructure.config.MailConfig.MailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class ResendEmailService {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailService.class);
    private static final String RESEND_URL = "https://api.resend.com/emails";

    private final MailProperties mailProperties;
    private final RestClient restClient;

    public ResendEmailService(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
        this.restClient = RestClient.create();
    }

    public boolean isConfigured() {
        return mailProperties.isConfigured();
    }

    public void sendHtml(String to, String subject, String html) {
        if (!mailProperties.isConfigured()) {
            log.warn("Resend no configurado (RESEND_API_KEY vacío); no se envió correo a {}", to);
            return;
        }
        if (to == null || to.isBlank()) {
            log.warn("Destinatario vacío; no se envió correo");
            return;
        }

        Map<String, Object> body = Map.of(
                "from", mailProperties.fromHeader(),
                "to", List.of(to.trim()),
                "subject", subject,
                "html", html
        );

        try {
            restClient.post()
                    .uri(RESEND_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + mailProperties.getResendApiKey())
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Correo Resend enviado a {} — {}", to, subject);
        } catch (Exception e) {
            log.error("Error enviando correo Resend a {}: {}", to, e.getMessage(), e);
            throw e;
        }
    }
}
