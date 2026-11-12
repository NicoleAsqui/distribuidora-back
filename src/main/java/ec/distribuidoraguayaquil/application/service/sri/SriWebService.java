package ec.distribuidoraguayaquil.application.service.sri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/** Servicios web offline del SRI: recepción y autorización de comprobantes. */
public class SriWebService {

    public record Respuesta(String estado, List<String> mensajes, String numeroAutorizacion, String fechaAutorizacion) {}

    private final String base;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();

    public SriWebService(String ambiente) {
        this.base = "2".equals(ambiente)
                ? "https://cel.sri.gob.ec/comprobantes-electronicos-ws/"
                : "https://celcer.sri.gob.ec/comprobantes-electronicos-ws/";
    }

    public Respuesta enviar(String xmlFirmado) {
        String b64 = Base64.getEncoder().encodeToString(xmlFirmado.getBytes(StandardCharsets.UTF_8));
        String soap = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ec="http://ec.gob.sri.ws.recepcion">
                <soapenv:Header/><soapenv:Body><ec:validarComprobante><xml>%s</xml></ec:validarComprobante></soapenv:Body>
                </soapenv:Envelope>""".formatted(b64);
        Document d = post("RecepcionComprobantesOffline", soap);
        return new Respuesta(first(d.getDocumentElement(), "estado"), mensajes(d.getDocumentElement()), null, null);
    }

    public Respuesta autorizacion(String claveAcceso) {
        String soap = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ec="http://ec.gob.sri.ws.autorizacion">
                <soapenv:Header/><soapenv:Body><ec:autorizacionComprobante><claveAccesoComprobante>%s</claveAccesoComprobante></ec:autorizacionComprobante></soapenv:Body>
                </soapenv:Envelope>""".formatted(claveAcceso);
        Document d = post("AutorizacionComprobantesOffline", soap);
        NodeList auts = d.getElementsByTagName("autorizacion");
        if (auts.getLength() == 0) {
            return new Respuesta("EN PROCESO", List.of(), null, null);
        }
        Element a = (Element) auts.item(0);
        return new Respuesta(first(a, "estado"), mensajes(a), first(a, "numeroAutorizacion"), first(a, "fechaAutorizacion"));
    }

    private Document post(String servicio, String soap) {
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(base + servicio))
                    .timeout(Duration.ofSeconds(40))
                    .header("Content-Type", "text/xml; charset=utf-8")
                    .POST(HttpRequest.BodyPublishers.ofString(soap, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<byte[]> res = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return f.newDocumentBuilder().parse(new ByteArrayInputStream(res.body()));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo conectar con el SRI (" + servicio + "): " + e.getMessage(), e);
        }
    }

    private static String first(Element el, String tag) {
        NodeList n = el.getElementsByTagName(tag);
        return n.getLength() == 0 ? null : n.item(0).getTextContent().trim();
    }

    private static List<String> mensajes(Element el) {
        List<String> out = new ArrayList<>();
        NodeList ms = el.getElementsByTagName("mensaje");
        for (int i = 0; i < ms.getLength(); i++) {
            Element m = (Element) ms.item(i);
            if (m.getElementsByTagName("identificador").getLength() == 0) continue;
            String txt = first(m, "identificador") + " · " + first(m, "mensaje");
            String extra = first(m, "informacionAdicional");
            out.add(extra == null || extra.isBlank() ? txt : txt + " — " + extra);
        }
        return out;
    }
}
