package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.port.in.SiteConfigUseCase;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.PricingQuoteEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class QuoteAdminMailService {

    private static final Logger log = LoggerFactory.getLogger(QuoteAdminMailService.class);

    private final ResendEmailService resendEmailService;
    private final SiteConfigUseCase siteConfigUseCase;
    private final ObjectMapper objectMapper;
    private final String publicSiteUrl;

    public QuoteAdminMailService(
            ResendEmailService resendEmailService,
            SiteConfigUseCase siteConfigUseCase,
            ObjectMapper objectMapper,
            @Value("${PUBLIC_SITE_URL:}") String publicSiteUrl) {
        this.resendEmailService = resendEmailService;
        this.siteConfigUseCase = siteConfigUseCase;
        this.objectMapper = objectMapper;
        this.publicSiteUrl = publicSiteUrl == null ? "" : publicSiteUrl.trim();
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

    /**
     * Envía la cotización simple al correo del cliente (PDF adjunto).
     * Mensaje automático: no responder.
     */
    public void sendManualQuoteToClient(
            PricingQuoteEntity quote,
            String pdfBase64,
            String filename) {
        String to = safe(quote.getClientEmail()).trim();
        if (to.isBlank()) {
            throw new IllegalArgumentException("La cotización no tiene email de cliente");
        }
        if (!resendEmailService.isConfigured()) {
            throw new IllegalStateException("RESEND_API_KEY no configurada");
        }
        if (pdfBase64 == null || pdfBase64.isBlank()) {
            throw new IllegalArgumentException("Falta el PDF de la cotización");
        }
        String subject = "Cotización " + quote.getCode() + " — Distribuidora Guayaquil";
        String attachName = (filename == null || filename.isBlank())
                ? quote.getCode() + ".pdf"
                : filename.trim();
        if (!attachName.toLowerCase().endsWith(".pdf")) {
            attachName = attachName + ".pdf";
        }
        String b64 = pdfBase64.trim();
        int comma = b64.indexOf(',');
        if (b64.startsWith("data:") && comma > 0) {
            b64 = b64.substring(comma + 1);
        }
        resendEmailService.sendHtml(
                to,
                subject,
                buildClientManualHtml(quote),
                List.of(new ResendEmailService.Attachment(attachName, b64))
        );
    }

    private String buildClientManualHtml(PricingQuoteEntity quote) {
        StringBuilder itemsHtml = new StringBuilder();
        try {
            JsonNode items = objectMapper.readTree(quote.getItemsJson() == null ? "[]" : quote.getItemsJson());
            if (items.isArray()) {
                for (JsonNode item : items) {
                    String label = text(item, "label");
                    int qty = item.path("cantidad").asInt(0);
                    BigDecimal unit = decimal(item.path("precioUnitario"));
                    BigDecimal line = decimal(item.path("lineTotal"));
                    if (line.signum() == 0) {
                        line = unit.multiply(BigDecimal.valueOf(qty));
                    }
                    itemsHtml.append("<tr>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;'>")
                            .append(esc(label.isBlank() ? "Producto" : label)).append("</td>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;text-align:center;'>")
                            .append(qty).append("</td>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;text-align:right;'>$")
                            .append(money(unit)).append("</td>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;text-align:right;'><strong>$")
                            .append(money(line)).append("</strong></td>")
                            .append("</tr>");
                }
            }
        } catch (Exception e) {
            itemsHtml.append("<tr><td colspan='4' style='padding:10px;'>Ver detalle en el PDF adjunto</td></tr>");
        }

        boolean withIva = Boolean.TRUE.equals(quote.getRequiresInvoice());
        BigDecimal subtotal = quote.getSubtotal() == null ? BigDecimal.ZERO : quote.getSubtotal();
        BigDecimal iva = quote.getIva() == null ? BigDecimal.ZERO : quote.getIva();
        BigDecimal total = quote.getTotal() == null ? BigDecimal.ZERO : quote.getTotal();
        String client = safe(quote.getClientName()).isBlank() ? "cliente" : quote.getClientName().trim();

        return """
                <div style="font-family:Arial,sans-serif;color:#0f172a;max-width:640px;">
                  <div style="background:#f8fafc;border:1px solid #e2e8f0;border-radius:8px;padding:12px 14px;margin:0 0 18px;">
                    <p style="margin:0;font-size:13px;color:#475569;line-height:1.45;">
                      <strong>Mensaje automático del sistema.</strong>
                      Este correo se generó al emitir una cotización desde Distribuidora Guayaquil.
                      <strong>Por favor no responder a este mensaje</strong> — la bandeja no es monitoreada.
                      Si necesita coordinar, contáctenos por los canales habituales de la empresa.
                    </p>
                  </div>
                  <h2 style="margin:0 0 8px;">Cotización %s</h2>
                  <p style="margin:0 0 16px;color:#64748b;">Hola %s, adjuntamos tu cotización en PDF.</p>
                  <table style="width:100%%;border-collapse:collapse;font-size:14px;margin-bottom:12px;">
                    <thead>
                      <tr style="background:#f8fafc;">
                        <th style="padding:10px;border:1px solid #e2e8f0;text-align:left;">Producto</th>
                        <th style="padding:10px;border:1px solid #e2e8f0;text-align:center;">Cant.</th>
                        <th style="padding:10px;border:1px solid #e2e8f0;text-align:right;">P. unit.</th>
                        <th style="padding:10px;border:1px solid #e2e8f0;text-align:right;">Subtotal</th>
                      </tr>
                    </thead>
                    <tbody>%s</tbody>
                  </table>
                  <p style="margin:8px 0 0;text-align:right;color:#64748b;">Subtotal: <strong>$%s</strong></p>
                  %s
                  <p style="margin:4px 0 0;text-align:right;font-size:18px;">Total: <strong>$%s USD</strong></p>
                  <p style="margin:20px 0 0;color:#94a3b8;font-size:12px;">
                    Este documento no es un comprobante contable formal. Validez: 1 semana desde la emisión.
                  </p>
                </div>
                """.formatted(
                esc(quote.getCode()),
                esc(client),
                itemsHtml,
                money(subtotal),
                withIva
                        ? "<p style=\"margin:4px 0 0;text-align:right;color:#64748b;\">IVA: <strong>$"
                            + money(iva) + "</strong></p>"
                        : "<p style=\"margin:4px 0 0;text-align:right;color:#64748b;\">Sin IVA</p>",
                money(total)
        );
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
                    if (unit.signum() == 0) {
                        unit = decimal(item.path("precioUnitario"));
                    }
                    BigDecimal line = decimal(pricing.path("lineTotal"));
                    if (line.signum() == 0) {
                        line = decimal(item.path("lineTotal"));
                    }
                    String notes = text(item, "notes");
                    boolean manual = !item.has("pricing") && item.has("precioUnitario");

                    itemsHtml.append("<tr>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;'>").append(i++).append("</td>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;'>")
                            .append(esc(label.isBlank() ? material : label)).append("<br/>")
                            .append("<span style='color:#64748b;font-size:12px;'>");
                    if (manual) {
                        itemsHtml.append("qty ").append(qty);
                    } else {
                        itemsHtml.append(esc(material)).append(" · ")
                                .append(fmt(largo)).append("×").append(fmt(ancho)).append("×").append(fmt(alto))
                                .append(" cm · qty ").append(qty);
                    }
                    itemsHtml.append("</span>");
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
                  %s
                  <p style="margin:12px 0 0;color:#64748b;font-size:13px;">También puedes revisar en Admin → Cotizaciones.</p>
                </div>
                """.formatted(
                esc(quote.getCode()),
                esc(safe(quote.getClientName())),
                esc(safe(quote.getClientPhone())),
                esc(safe(quote.getClientEmail()).isBlank() ? "-" : quote.getClientEmail()),
                esc(safe(quote.getDeliveryDate()).isBlank() ? "-" : quote.getDeliveryDate()),
                esc(safe(quote.getNotes()).isBlank() ? "-" : quote.getNotes()),
                itemsHtml,
                money(quote.getTotal() == null ? BigDecimal.ZERO : quote.getTotal()),
                editButtonHtml(quote)
        );
    }

    private String editButtonHtml(PricingQuoteEntity quote) {
        String token = quote.getEditToken();
        if (token == null || token.isBlank() || publicSiteUrl.isBlank()) {
            return "<p style=\"margin:16px 0 0;color:#64748b;font-size:13px;\">"
                    + "Configura PUBLIC_SITE_URL en el backend para el botón Editar PDF en el correo.</p>";
        }
        String base = publicSiteUrl.replaceAll("/+$", "");
        String url = base + "/admin/cotizacion/edit/" + escUrl(quote.getId())
                + "?token=" + escUrl(token);
        return """
                <p style="margin:20px 0 0;">
                  <a href="%s" style="display:inline-block;background:#00B8C4;color:#fff;text-decoration:none;
                  font-weight:700;padding:12px 20px;border-radius:8px;font-size:15px;">
                    Editar PDF / precios
                  </a>
                </p>
                <p style="margin:8px 0 0;color:#64748b;font-size:12px;">
                  Ajusta precios sin perder cliente, medidas ni modelos. Luego genera el PDF de nuevo.
                </p>
                """.formatted(url);
    }

    private static String escUrl(String s) {
        return java.net.URLEncoder.encode(safe(s), java.nio.charset.StandardCharsets.UTF_8);
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
