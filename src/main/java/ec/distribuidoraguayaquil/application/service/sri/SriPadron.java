package ec.distribuidoraguayaquil.application.service.sri;

import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Consulta pública del SRI (catastro) para autocompletar los datos del cliente por cédula o RUC. */
@Service
public class SriPadron {

    private static final String BASE = "https://srienlinea.sri.gob.ec/sri-catastro-sujeto-servicio-internet/rest/";

    private final ObjectMapper objectMapper;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();
    private final Map<String, Map<String, Object>> cache = new ConcurrentHashMap<>();

    public SriPadron(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** {encontrado, tipoIdentificacion, identificacion, razonSocial, direccion, estado, regimen}. */
    public Map<String, Object> buscar(String identificacion) {
        String id = identificacion == null ? "" : identificacion.replaceAll("\\D", "");
        if (!id.matches("\\d{10}|\\d{10}001")) {
            return Map.of("encontrado", false, "mensaje", "Escribe una cédula de 10 dígitos o un RUC de 13.");
        }
        return cache.computeIfAbsent(id, this::consultar);
    }

    private Map<String, Object> consultar(String id) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("identificacion", id);
        try {
            if (id.length() == 13) {
                out.put("tipoIdentificacion", "04");
                JsonNode arr = get("ConsolidadoContribuyente/obtenerPorNumerosRuc?&ruc=" + id);
                JsonNode c = arr != null && arr.isArray() && !arr.isEmpty() ? arr.get(0) : null;
                if (c == null) return noEncontrado(out);
                out.put("razonSocial", c.path("razonSocial").asString(""));
                out.put("estado", c.path("estadoContribuyenteRuc").asString(""));
                out.put("regimen", c.path("regimen").asString(""));
                JsonNode est = get("Establecimiento/consultarPorNumeroRuc?numeroRuc=" + id);
                if (est != null && est.isArray()) {
                    for (JsonNode e : est) {
                        if ("SI".equals(e.path("matriz").asString(""))) {
                            out.put("direccion", e.path("direccionCompleta").asString("").replaceAll("\\s+", " ").trim());
                        }
                    }
                }
            } else {
                out.put("tipoIdentificacion", "05");
                JsonNode p = get("Persona/obtenerPorTipoIdentificacion?numeroIdentificacion=" + id + "&tipoIdentificacion=C");
                if (p == null || p.path("nombreCompleto").asString("").isBlank()) return noEncontrado(out);
                out.put("razonSocial", p.path("nombreCompleto").asString(""));
            }
            out.put("encontrado", true);
            return out;
        } catch (Exception e) {
            // No se guarda en caché: se puede reintentar cuando el SRI responda.
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_GATEWAY,
                    "El SRI no respondió. Escribe los datos a mano o intenta de nuevo.");
        }
    }

    private Map<String, Object> noEncontrado(Map<String, Object> out) {
        out.put("encontrado", false);
        out.put("mensaje", "No aparece en el SRI. Revisa el número o escribe los datos a mano.");
        return out;
    }

    private JsonNode get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
                .timeout(Duration.ofSeconds(12))
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();
        HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 204 || res.body() == null || res.body().isBlank()) return null;
        if (res.statusCode() != 200) throw new IllegalStateException("SRI " + res.statusCode());
        return objectMapper.readTree(res.body());
    }
}
