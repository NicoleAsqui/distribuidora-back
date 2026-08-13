package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.port.in.SiteConfigUseCase;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.PricingQuoteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class QuoteAdminMailService {

    private static final Logger log = LoggerFactory.getLogger(QuoteAdminMailService.class);

    private final ResendEmailService resendEmailService;
    private final SiteConfigUseCase siteConfigUseCase;
    private final ObjectMapper objectMapper;

    public QuoteAdminMailService(
            ResendEmailService resendEmailService,
            SiteConfigUseCase siteConfigUseCase,
            ObjectMapper objectMapper) {
        this.resendEmailService = resendEmailService;
        this.siteConfigUseCase = siteConfigUseCase;
        this.objectMapper = objectMapper;
    }

    public void notifyAdminNewWebQuote(PricingQuoteEntity quote) {
        if (!"web".equalsIgnoreCase(quote.getSource())) {
            return;
        }
        String to = siteConfigUseCase.get().emailAsesor();
        if (to == null || to.isBlank()) {
            log.warn("emailAsesor vacío; no se notifica cotización {}", quote.getCode());
            return;
        }
        if (!resendEmailService.isConfigured()) {
            log.warn("RESEND_API_KEY no configurada; cotización {} guardada sin email", quote.getCode());
            return;
        }

        String subject = "Cotización " + quote.getCode() + " — " + safe(quote.getClientName());
        try {
            resendEmailService.sendHtml(to, subject, buildHtml(quote));
        } catch (Exception e) {
            // No tumbar el POST del cliente si falla el mail
            log.error("No se pudo notificar cotización {}: {}", quote.getCode(), e.getMessage());
        }
    }

    private String buildHtml(PricingQuoteEntity quote) {
        StringBuilder itemsHtml = new StringBuilder();
        try {
            JsonNode items = objectMapper.readTree(quote.getItemsJson() == null ? "[]" : quote.getItemsJson());
            if (items.isArray()) {
                int i = 1;
                for (JsonNode item : items) {
                    String label = text(item, "label");
                    String material = text(item, "material");
                    int qty = item.path("cantidad").asInt(0);
                    BigDecimal largo = decimal(item.path("largo"));
                    BigDecimal ancho = decimal(item.path("ancho"));
                    BigDecimal alto = decimal(item.path("altoBase"));
                    JsonNode pricing = item.path("pricing");
                    BigDecimal unit = decimal(pricing.path("precioUnidad"));
                    BigDecimal line = decimal(pricing.path("lineTotal"));
                    String notes = text(item, "notes");

                    itemsHtml.append("<tr>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;'>").append(i++).append("</td>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;'>")
                            .append(esc(label.isBlank() ? material : label)).append("<br/>")
                            .append("<span style='color:#64748b;font-size:12px;'>")
                            .append(esc(material)).append(" · ")
                            .append(fmt(largo)).append("×").append(fmt(ancho)).append("×").append(fmt(alto))
                            .append(" cm · qty ").append(qty)
                            .append("</span>");
                    if (!notes.isBlank()) {
                        itemsHtml.append("<br/><span style='color:#64748b;font-size:12px;'>Notas: ")
                                .append(esc(notes)).append("</span>");
                    }
                    itemsHtml.append("</td>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;text-align:right;'>$")
                            .append(money(unit)).append("</td>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;text-align:right;'><strong>$")
                            .append(money(line)).append("</strong></td>")
                            .append("</tr>");
                }
            }
        } catch (Exception e) {
            itemsHtml.append("<tr><td colspan='4' style='padding:10px;'>No se pudieron leer los ítems</td></tr>");
        }

        return """
                <div style="font-family:Arial,sans-serif;color:#0f172a;max-width:640px;">
                  <h2 style="margin:0 0 8px;">Nueva cotización web</h2>
                  <p style="margin:0 0 16px;color:#64748b;">Código <strong>%s</strong></p>
                  <table style="width:100%%;border-collapse:collapse;margin-bottom:18px;">
                    <tr><td style="padding:6px 0;color:#64748b;">Cliente</td><td style="padding:6px 0;"><strong>%s</strong></td></tr>
                    <tr><td style="padding:6px 0;color:#64748b;">Teléfono</td><td style="padding:6px 0;">%s</td></tr>
                    <tr><td style="padding:6px 0;color:#64748b;">Email</td><td style="padding:6px 0;">%s</td></tr>
                    <tr><td style="padding:6px 0;color:#64748b;">Entrega tentativa</td><td style="padding:6px 0;">%s</td></tr>
                    <tr><td style="padding:6px 0;color:#64748b;">Notas</td><td style="padding:6px 0;">%s</td></tr>
                  </table>
                  <table style="width:100%%;border-collapse:collapse;font-size:14px;">
                    <thead>
                      <tr style="background:#f8fafc;">
                        <th style="padding:10px;border:1px solid #e2e8f0;text-align:left;">#</th>
                        <th style="padding:10px;border:1px solid #e2e8f0;text-align:left;">Caja</th>
                        <th style="padding:10px;border:1px solid #e2e8f0;text-align:right;">P. unidad</th>
                        <th style="padding:10px;border:1px solid #e2e8f0;text-align:right;">Subtotal</th>
                      </tr>
                    </thead>
                    <tbody>%s</tbody>
                  </table>
                  <p style="margin:18px 0 0;font-size:18px;">Total estimado: <strong>$%s USD</strong></p>
                  <p style="margin:12px 0 0;color:#64748b;font-size:13px;">Revisa el detalle completo en Admin → Cotizaciones.</p>
                </div>
                """.formatted(
                esc(quote.getCode()),
                esc(safe(quote.getClientName())),
                esc(safe(quote.getClientPhone())),
                esc(safe(quote.getClientEmail()).isBlank() ? "-" : quote.getClientEmail()),
                esc(safe(quote.getDeliveryDate()).isBlank() ? "-" : quote.getDeliveryDate()),
                esc(safe(quote.getNotes()).isBlank() ? "-" : quote.getNotes()),
                itemsHtml,
                money(quote.getTotal() == null ? BigDecimal.ZERO : quote.getTotal())
        );
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.path(field);
        return v.isMissingNode() || v.isNull() ? "" : v.asString("");
    }

    private static BigDecimal decimal(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(node.asString("0"));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private static String money(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String fmt(BigDecimal v) {
        return v.stripTrailingZeros().toPlainString();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String esc(String s) {
        return safe(s)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
