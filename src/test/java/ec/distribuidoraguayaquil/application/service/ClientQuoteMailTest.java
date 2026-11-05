package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.PricingQuoteEntity;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** Genera el HTML del correo al cliente (y lo guarda si se pasa -Dmail.preview=ruta). */
class ClientQuoteMailTest {
    @Test
    void correoConBotonWhatsappYAvisoNoResponder() throws Exception {
        PricingQuoteEntity q = new PricingQuoteEntity();
        q.setCode("COT-123456");
        q.setClientName("María Pérez");
        q.setRequiresInvoice(true);
        q.setSubtotal(new BigDecimal("100.00"));
        q.setIva(new BigDecimal("15.00"));
        q.setTotal(new BigDecimal("115.00"));
        q.setItemsJson("[{\"label\":\"Caja tapa imán\",\"cantidad\":50,\"precioUnitario\":2,\"lineTotal\":100},"
                + "{\"disenoNombre\":\"Caja Corazón\",\"largo\":20,\"ancho\":15,\"altoBase\":8,\"cantidad\":12,"
                + "\"pricing\":{\"precioUnidad\":1.5,\"lineTotal\":18}}]");
        QuoteAdminMailService svc = new QuoteAdminMailService(null, null, new ObjectMapper(), "");
        String html = svc.buildClientManualHtml(q);
        assertTrue(html.contains("https://wa.me/593983867216?text="));
        assertTrue(html.contains("no respondas a este mensaje"));
        assertTrue(html.contains("Gracias por preferirnos"));
        assertTrue(html.contains("¡Saludos María!"));
        assertTrue(html.contains("20 × 15 × 8 cm"));
        String out = System.getProperty("mail.preview");
        if (out != null && !out.isBlank()) {
            Files.writeString(Path.of(out), html);
        }
    }
}
