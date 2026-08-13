package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.EmitirFacturaAdminRequest;
import ec.distribuidoraguayaquil.infrastructure.config.SriFacturacionConfig.SriFacturacionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FacturacionService {

    private static final Logger log = LoggerFactory.getLogger(FacturacionService.class);
    private static final BigDecimal IVA_TARIFA = new BigDecimal("15");
    private static final BigDecimal IVA_FACTOR = new BigDecimal("0.15");

    private final SriFacturacionProperties props;
    private final RestClient restClient;

    public FacturacionService(SriFacturacionProperties props) {
        this.props = props;
        this.restClient = RestClient.create();
    }

    public Map<String, Object> publicConfig() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("configured", props.isConfigured());
        out.put("ambiente", props.getAmbiente() == null || props.getAmbiente().isBlank() ? "1" : props.getAmbiente());
        SriFacturacionProperties.Emisor e = props.getEmisor();
        Map<String, Object> emisor = new LinkedHashMap<>();
        emisor.put("rucMasked", maskRuc(e.getRuc()));
        emisor.put("razonSocial", blankToEmpty(e.getRazonSocial()));
        emisor.put("nombreComercial", blankToEmpty(e.getNombreComercial()));
        emisor.put("dirMatriz", blankToEmpty(e.getDirMatriz()));
        emisor.put("dirEstablecimiento", blankToEmpty(e.getDirEstablecimiento()));
        emisor.put("establecimiento", blankToEmpty(e.getEstablecimiento()));
        emisor.put("puntoEmision", blankToEmpty(e.getPuntoEmision()));
        emisor.put("obligadoContabilidad", blankToEmpty(e.getObligadoContabilidad()));
        out.put("emisor", emisor);
        return out;
    }

    public Map<String, Object> emitir(EmitirFacturaAdminRequest request) {
        if (!props.isConfigured()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Facturación SRI no configurada. Define SRI_API_URL, SRI_API_TOKEN y datos del emisor.");
        }

        Map<String, Object> body = buildSriPayload(request);
        String base = props.getApiUrl().replaceAll("/$", "");
        String url = base + "/sri/emitir/factura";

        try {
            Map<String, Object> response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + props.getApiToken().trim())
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
            if (response == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Respuesta vacía de la API SRI");
            }
            return response;
        } catch (RestClientResponseException ex) {
            log.warn("API SRI error {}: {}", ex.getStatusCode().value(), ex.getResponseBodyAsString());
            String detail = ex.getResponseBodyAsString();
            if (detail == null || detail.isBlank()) {
                detail = ex.getStatusText();
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "API SRI (" + ex.getStatusCode().value() + "): " + truncate(detail, 500));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error al emitir factura SRI", ex);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo contactar la API SRI: " + ex.getMessage());
        }
    }

    private Map<String, Object> buildSriPayload(EmitirFacturaAdminRequest request) {
        SriFacturacionProperties.Emisor e = props.getEmisor();
        Map<String, Object> root = new LinkedHashMap<>();
        String ambiente = props.getAmbiente() == null || props.getAmbiente().isBlank() ? "1" : props.getAmbiente().trim();
        root.put("ambiente", ambiente);
        root.put("tipoEmision", "1");
        root.put("fechaEmision", request.fechaEmision());

        Map<String, Object> emisor = new LinkedHashMap<>();
        emisor.put("ruc", e.getRuc().trim());
        emisor.put("razonSocial", e.getRazonSocial().trim());
        if (e.getNombreComercial() != null && !e.getNombreComercial().isBlank()) {
            emisor.put("nombreComercial", e.getNombreComercial().trim());
        }
        emisor.put("dirMatriz", e.getDirMatriz().trim());
        if (e.getDirEstablecimiento() != null && !e.getDirEstablecimiento().isBlank()) {
            emisor.put("dirEstablecimiento", e.getDirEstablecimiento().trim());
        }
        emisor.put("establecimiento", blankOr(e.getEstablecimiento(), "001"));
        emisor.put("puntoEmision", blankOr(e.getPuntoEmision(), "001"));
        emisor.put("obligadoContabilidad", blankOr(e.getObligadoContabilidad(), "SI"));
        root.put("emisor", emisor);

        EmitirFacturaAdminRequest.CompradorDto c = request.comprador();
        Map<String, Object> comprador = new LinkedHashMap<>();
        comprador.put("tipoIdentificacion", c.tipoIdentificacion().trim());
        comprador.put("identificacion", c.identificacion().trim());
        comprador.put("razonSocial", c.razonSocial().trim());
        if (c.direccion() != null && !c.direccion().isBlank()) comprador.put("direccion", c.direccion().trim());
        if (c.telefono() != null && !c.telefono().isBlank()) comprador.put("telefono", c.telefono().trim());
        if (c.email() != null && !c.email().isBlank()) comprador.put("email", c.email().trim());
        root.put("comprador", comprador);

        List<Map<String, Object>> detalles = new ArrayList<>();
        BigDecimal totalConIva = BigDecimal.ZERO;

        for (EmitirFacturaAdminRequest.DetalleLineaDto d : request.detalles()) {
            BigDecimal cantidad = d.cantidad();
            BigDecimal precio = d.precioUnitario();
            BigDecimal descuento = d.descuento() == null ? BigDecimal.ZERO : d.descuento();
            BigDecimal base = cantidad.multiply(precio).subtract(descuento).setScale(2, RoundingMode.HALF_UP);
            if (base.compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La base imponible de un ítem no puede ser negativa");
            }
            BigDecimal iva = base.multiply(IVA_FACTOR).setScale(2, RoundingMode.HALF_UP);
            totalConIva = totalConIva.add(base).add(iva);

            Map<String, Object> det = new LinkedHashMap<>();
            det.put("codigoPrincipal", d.codigoPrincipal().trim());
            det.put("descripcion", d.descripcion().trim());
            det.put("cantidad", cantidad);
            det.put("precioUnitario", precio);
            det.put("descuento", descuento.setScale(2, RoundingMode.HALF_UP));

            Map<String, Object> imp = new LinkedHashMap<>();
            imp.put("codigo", "2");
            imp.put("codigoPorcentaje", "4");
            imp.put("tarifa", IVA_TARIFA);
            imp.put("baseImponible", base);
            imp.put("valor", iva);
            det.put("impuestos", List.of(imp));
            detalles.add(det);
        }
        root.put("detalles", detalles);

        String formaPago = request.formaPago() == null || request.formaPago().isBlank()
                ? "01"
                : request.formaPago().trim();
        Map<String, Object> pago = new LinkedHashMap<>();
        pago.put("formaPago", formaPago);
        pago.put("total", totalConIva.setScale(2, RoundingMode.HALF_UP));
        root.put("pagos", List.of(pago));

        return root;
    }

    private static String maskRuc(String ruc) {
        if (ruc == null || ruc.isBlank()) return "";
        String t = ruc.trim();
        if (t.length() < 5) return "****";
        return t.substring(0, 3) + "*******" + t.substring(t.length() - 3);
    }

    private static String blankToEmpty(String v) {
        return v == null ? "" : v.trim();
    }

    private static String blankOr(String v, String fallback) {
        return v == null || v.isBlank() ? fallback : v.trim();
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "…";
    }
}
