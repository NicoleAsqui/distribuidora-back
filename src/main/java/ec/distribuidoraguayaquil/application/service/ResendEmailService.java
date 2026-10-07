package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.infrastructure.config.MailConfig.MailProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        sendHtml(to, subject, html, List.of());
    }

    public record Attachment(String filename, String contentBase64) {}

    public void sendHtml(String to, String subject, String html, List<Attachment> attachments) {
        if (!mailProperties.isConfigured()) {
            log.warn("Resend no configurado (RESEND_API_KEY vacío); no se envió correo a {}", to);
            return;
        }
        if (to == null || to.isBlank()) {
            log.warn("Destinatario vacío; no se envió correo");
            return;
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("from", mailProperties.fromHeader());
        body.put("to", List.of(to.trim()));
        body.put("subject", subject);
        body.put("html", html);
        if (attachments != null && !attachments.isEmpty()) {
            List<Map<String, String>> atts = new ArrayList<>();
            for (Attachment a : attachments) {
                if (a == null || a.filename() == null || a.filename().isBlank()
                        || a.contentBase64() == null || a.contentBase64().isBlank()) {
                    continue;
                }
                atts.add(Map.of(
                        "filename", a.filename().trim(),
                        "content", a.contentBase64().replaceAll("\\s", "")
                ));
            }
            if (!atts.isEmpty()) {
                body.put("attachments", atts);
            }
        }

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
