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
        String subject = "Tu cotización " + quote.getCode() + " · Distribuidora Guayaquil";
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

    /** WhatsApp de atención al cliente (botón del correo). */
    private static final String CLIENT_WHATSAPP = "593983867216";

    /**
     * Correo al cliente con la cotización adjunta. Diseño con tablas e estilos en línea
     * (compatible con Gmail, Outlook y celulares). Correo automático: se pide escribir por WhatsApp.
     */
    String buildClientManualHtml(PricingQuoteEntity quote) {
        StringBuilder rows = new StringBuilder();
        try {
            JsonNode items = objectMapper.readTree(quote.getItemsJson() == null ? "[]" : quote.getItemsJson());
            if (items.isArray()) {
                for (JsonNode item : items) {
                    String label = text(item, "disenoNombre");
                    if (label.isBlank()) {
                        label = text(item, "label");
                    }
                    int qty = item.path("cantidad").asInt(0);
                    JsonNode pricing = item.path("pricing");
                    BigDecimal unit = decimal(pricing.path("precioUnidad"));
                    if (unit.signum() == 0) {
                        unit = decimal(item.path("precioUnitario"));
                    }
                    BigDecimal line = decimal(pricing.path("lineTotal"));
                    if (line.signum() == 0) {
                        line = decimal(item.path("lineTotal"));
                    }
                    if (line.signum() == 0) {
                        line = unit.multiply(BigDecimal.valueOf(qty));
                    }
                    String medida = "";
                    if (item.has("largo")) {
                        medida = decimal(item.path("largo")).stripTrailingZeros().toPlainString() + " × "
                                + decimal(item.path("ancho")).stripTrailingZeros().toPlainString() + " × "
                                + decimal(item.path("altoBase")).stripTrailingZeros().toPlainString() + " cm";
                    }
                    rows.append("<tr>")
                            .append("<td style=\"padding:12px 14px;border-bottom:1px solid #eef2f6;\">")
                            .append("<div style=\"font-weight:700;color:#1a2744;font-size:14px;\">")
                            .append(esc(label.isBlank() ? "Producto" : label)).append("</div>")
                            .append(medida.isBlank() ? "" : "<div style=\"color:#64748b;font-size:12px;margin-top:2px;\">"
                                    + esc(medida) + "</div>")
                            .append("</td>")
                            .append("<td align=\"center\" style=\"padding:12px 8px;border-bottom:1px solid #eef2f6;color:#334155;font-size:14px;\">")
                            .append(qty).append("</td>")
                            .append("<td align=\"right\" style=\"padding:12px 14px;border-bottom:1px solid #eef2f6;color:#1a2744;font-size:14px;font-weight:700;white-space:nowrap;\">$")
                            .append(money(line)).append("</td>")
                            .append("</tr>");
                }
            }
        } catch (Exception e) {
            rows.setLength(0);
        }
        if (rows.isEmpty()) {
            rows.append("<tr><td colspan=\"3\" style=\"padding:14px;color:#64748b;font-size:14px;\">")
                    .append("Encuentra el detalle completo en el PDF adjunto.</td></tr>");
        }

        boolean withIva = Boolean.TRUE.equals(quote.getRequiresInvoice());
        BigDecimal subtotal = quote.getSubtotal() == null ? BigDecimal.ZERO : quote.getSubtotal();
        BigDecimal iva = quote.getIva() == null ? BigDecimal.ZERO : quote.getIva();
        BigDecimal total = quote.getTotal() == null ? BigDecimal.ZERO : quote.getTotal();
        String client = safe(quote.getClientName()).isBlank() ? "" : " " + quote.getClientName().trim().split("\\s+")[0];
        String waText = "Hola, tengo una consulta sobre mi cotización " + safe(quote.getCode()) + " de Distribuidora Guayaquil.";
        String waUrl = "https://wa.me/" + CLIENT_WHATSAPP + "?text="
                + java.net.URLEncoder.encode(waText, java.nio.charset.StandardCharsets.UTF_8).replace("+", "%20");
        String ivaRow = withIva
                ? "<tr><td style=\"padding:4px 0;color:#64748b;font-size:14px;\">IVA 15%</td>"
                    + "<td align=\"right\" style=\"padding:4px 0;color:#334155;font-size:14px;\">$" + money(iva) + "</td></tr>"
                : "";

        return """
                <!doctype html>
                <html lang="es"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
                <title>Cotización %1$s</title></head>
                <body style="margin:0;padding:0;background:#f3f6f9;font-family:Arial,Helvetica,sans-serif;">
                <div style="display:none;max-height:0;overflow:hidden;color:#f3f6f9;">Gracias por preferirnos. Adjuntamos tu cotización %1$s.</div>
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background:#f3f6f9;padding:24px 12px;">
                  <tr><td align="center">
                    <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width:600px;background:#ffffff;border-radius:18px;overflow:hidden;box-shadow:0 8px 24px rgba(26,39,68,.08);">
                      <tr><td style="background:#1a2744;padding:26px 28px;">
                        <div style="color:#5fe0e8;font-size:12px;font-weight:700;letter-spacing:2px;text-transform:uppercase;">Distribuidora Guayaquil</div>
                        <div style="color:#ffffff;font-size:24px;font-weight:800;margin-top:6px;">Tu cotización está lista ✨</div>
                        <div style="color:#c8d3e6;font-size:14px;margin-top:4px;">Código <strong style="color:#ffffff;">%1$s</strong></div>
                      </td></tr>
                      <tr><td style="padding:26px 28px 6px;">
                        <p style="margin:0 0 10px;font-size:17px;color:#1a2744;font-weight:700;">¡Saludos%2$s!</p>
                        <p style="margin:0 0 6px;font-size:15px;line-height:1.6;color:#334155;">
                          Gracias por preferirnos. Adjuntamos tu cotización en <strong>PDF</strong> con todo el detalle.
                          Revísala con calma y cuéntanos si tienes alguna duda; quedamos al pendiente para realizar tu pedido.
                        </p>
                      </td></tr>
                      <tr><td style="padding:14px 28px 4px;">
                        <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="border:1px solid #eef2f6;border-radius:14px;overflow:hidden;">
                          <tr style="background:#f8fafc;">
                            <th align="left" style="padding:10px 14px;font-size:11px;letter-spacing:1px;text-transform:uppercase;color:#64748b;">Producto</th>
                            <th align="center" style="padding:10px 8px;font-size:11px;letter-spacing:1px;text-transform:uppercase;color:#64748b;">Cant.</th>
                            <th align="right" style="padding:10px 14px;font-size:11px;letter-spacing:1px;text-transform:uppercase;color:#64748b;">Total</th>
                          </tr>
                          %3$s
                        </table>
                      </td></tr>
                      <tr><td style="padding:10px 28px 0;">
                        <table role="presentation" width="100%%" cellpadding="0" cellspacing="0">
                          <tr><td style="padding:4px 0;color:#64748b;font-size:14px;">Subtotal</td>
                              <td align="right" style="padding:4px 0;color:#334155;font-size:14px;">$%4$s</td></tr>
                          %5$s
                          <tr><td style="padding:10px 0 0;color:#1a2744;font-size:18px;font-weight:800;border-top:2px solid #e0f7f9;">Total</td>
                              <td align="right" style="padding:10px 0 0;color:#00a3ad;font-size:22px;font-weight:800;border-top:2px solid #e0f7f9;">$%6$s USD</td></tr>
                        </table>
                      </td></tr>
                      <tr><td style="padding:20px 28px 0;">
                        <div style="background:#fff8e1;border-left:4px solid #f5b400;border-radius:10px;padding:12px 14px;font-size:13px;line-height:1.55;color:#5b4a00;">
                          <strong>Tiempo de elaboración y entrega:</strong> pedidos pequeños, de 5 a 7 días hábiles;
                          pedidos grandes, de 1 a 2 semanas (no se consideran fines de semana ni feriados).
                        </div>
                        <div style="margin-top:10px;background:#fff1f1;border-left:4px solid #e5484d;border-radius:10px;padding:12px 14px;font-size:13px;line-height:1.55;color:#a51d22;">
                          <strong>Consideraciones:</strong> si requiere su pedido con urgencia, comuníquese con uno de nuestros
                          asesores, quien evaluará su caso. Le recomendamos programar sus pedidos con anticipación, idealmente
                          con 7 días hábiles, ya que así garantizamos el mejor resultado. Los pedidos urgentes se atienden como
                          excepción, sujetos a la demanda y a la disponibilidad de producción.
                        </div>
                      </td></tr>
                      <tr><td align="center" style="padding:26px 28px 8px;">
                        <a href="%7$s" target="_blank"
                           style="display:inline-block;background:#25d366;color:#ffffff;text-decoration:none;font-weight:800;font-size:16px;padding:15px 28px;border-radius:999px;">
                          💬 Escríbenos por WhatsApp
                        </a>
                        <div style="font-size:13px;color:#64748b;margin-top:10px;">+593 98 386 7216</div>
                      </td></tr>
                      <tr><td style="padding:18px 28px 26px;">
                        <div style="background:#f1f5f9;border-radius:12px;padding:12px 14px;font-size:12px;line-height:1.55;color:#64748b;text-align:center;">
                          Este es un <strong>correo automático</strong>, por favor <strong>no respondas a este mensaje</strong>.
                          Para cualquier consulta escríbenos por WhatsApp. Esta cotización tiene una validez de 1 semana.
                        </div>
                      </td></tr>
                    </table>
                    <div style="font-size:12px;color:#94a3b8;margin-top:14px;">Distribuidora Guayaquil · Cajas y empaques a todo el Ecuador</div>
                  </td></tr>
                </table>
                </body></html>
                """.formatted(
                esc(quote.getCode()),
                esc(client),
                rows,
                money(subtotal),
                ivaRow,
                money(total),
                esc(waUrl)
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
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;text-align:right;'>")
                            .append(unit.signum() == 0 ? "<em style='color:#b45309'>Por cotizar</em>" : "$" + money(unit))
                            .append("</td>")
                            .append("<td style='padding:10px;border:1px solid #e2e8f0;text-align:right;'><strong>")
                            .append(line.signum() == 0 ? "—" : "$" + money(line)).append("</strong></td>")
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
                  <p style="margin:18px 0 0;font-size:18px;">%s</p>
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
                quote.getTotal() == null || quote.getTotal().signum() == 0
                        ? "<strong style=\"color:#b45309\">Solicitud sin precios: ponlos en Admin → Cotizaciones → Poner precios.</strong>"
                        : "Total estimado: <strong>$" + money(quote.getTotal()) + " USD</strong>",
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
