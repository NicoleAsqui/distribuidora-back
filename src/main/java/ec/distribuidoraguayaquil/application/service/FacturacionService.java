package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.application.service.sri.SriAjustesStore;
import ec.distribuidoraguayaquil.application.service.sri.SriComprobante;
import ec.distribuidoraguayaquil.application.service.sri.SriFirma;
import ec.distribuidoraguayaquil.application.service.sri.SriFirmaStore;
import ec.distribuidoraguayaquil.application.service.sri.SriWebService;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.EmitirFacturaAdminRequest;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.FacturaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.FacturaProductoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.FacturaJpaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.FacturaProductoJpaRepository;
import ec.distribuidoraguayaquil.infrastructure.config.SriFacturacionConfig.SriFacturacionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
public class FacturacionService {

    private static final Logger log = LoggerFactory.getLogger(FacturacionService.class);
    private static final String CONSUMIDOR_FINAL = "9999999999999";
    private static final BigDecimal LIMITE_CONSUMIDOR_FINAL = new BigDecimal("50");

    private final SriAjustesStore ajustes;
    private final FacturaJpaRepository repository;
    private final ObjectMapper objectMapper;
    private final ResendEmailService mail;
    private final SriFirmaStore firmas;
    private final FacturaProductoJpaRepository productos;
    private final SecureRandom random = new SecureRandom();

    public FacturacionService(SriAjustesStore ajustes, FacturaJpaRepository repository,
                              ObjectMapper objectMapper, ResendEmailService mail, SriFirmaStore firmas,
                              FacturaProductoJpaRepository productos) {
        this.ajustes = ajustes;
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.mail = mail;
        this.firmas = firmas;
        this.productos = productos;
    }

    private SriFacturacionProperties props() {
        return ajustes.vigente();
    }

    public Map<String, Object> guardarAjustes(SriAjustesStore.Ajustes a, boolean confirmarProduccion, String actor) {
        ajustes.guardar(a, confirmarProduccion, actor);
        return publicConfig();
    }

    private List<String> missing() {
        List<String> m = new ArrayList<>(props().missing());
        if (firmas.info().isEmpty()) m.add("Firma electrónica (.p12)");
        return m;
    }

    public Map<String, Object> guardarFirma(byte[] p12, char[] clave, String actor) {
        try {
            return firmas.guardar(p12, clave, actor);
        } finally {
            java.util.Arrays.fill(clave, '\0');
        }
    }

    public Map<String, Object> publicConfig() {
        var e = props().getEmisor();
        Map<String, Object> out = new LinkedHashMap<>();
        List<String> missing = missing();
        out.put("configured", missing.isEmpty());
        out.put("missing", missing);
        out.put("firma", firmas.info().orElse(null));
        out.put("ambiente", props().getAmbiente());
        out.put("secuencialInicial", props().getSecuencialInicial());
        out.put("siguienteNumero", numero(siguienteSecuencial()));
        out.put("emailConfigured", mail.isConfigured());
        Map<String, Object> emisor = new LinkedHashMap<>();
        emisor.put("ruc", s(e.getRuc()));
        emisor.put("razonSocial", s(e.getRazonSocial()));
        emisor.put("nombreComercial", s(e.getNombreComercial()));
        emisor.put("dirMatriz", s(e.getDirMatriz()));
        emisor.put("dirEstablecimiento", s(e.getDirEstablecimiento()));
        emisor.put("establecimiento", s(e.getEstablecimiento()));
        emisor.put("puntoEmision", s(e.getPuntoEmision()));
        emisor.put("obligadoContabilidad", s(e.getObligadoContabilidad()));
        emisor.put("contribuyenteRimpe", s(e.getContribuyenteRimpe()));
        emisor.put("logoUrl", s(e.getLogoUrl()));
        out.put("emisor", emisor);
        return out;
    }

    public List<Map<String, Object>> list(String orderId) {
        List<FacturaEntity> rows = orderId == null || orderId.isBlank()
                ? repository.findAllByOrderByCreatedAtDesc()
                : repository.findByOrderIdOrderByCreatedAtDesc(orderId);
        return rows.stream().map(this::toDto).toList();
    }

    public Map<String, Object> get(String id) {
        return toDto(find(id));
    }

    /** Firma, envía al SRI y espera la autorización. */
    public synchronized Map<String, Object> emitir(EmitirFacturaAdminRequest req, String actor) {
        if (!props().isConfigured()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Facturación no configurada. Falta: " + String.join(", ", props().missing()));
        }
        char[] claveFirma = req.claveFirma() == null ? new char[0] : req.claveFirma().toCharArray();
        SriFirma firma;
        try {
            firma = firmas.abrir(claveFirma);
        } finally {
            java.util.Arrays.fill(claveFirma, '\0');
        }
        var e = props().getEmisor();
        SriComprobante.Comprador c = comprador(req.comprador());
        List<FacturaProductoEntity> usados = new ArrayList<>();
        List<SriComprobante.Linea> lineas = lineas(req.detalles(), usados);
        SriComprobante.Totales t = SriComprobante.totales(lineas);
        if ("07".equals(c.tipoIdentificacion()) && t.total().compareTo(LIMITE_CONSUMIDOR_FINAL) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A consumidor final solo se puede facturar hasta $50. Pide la cédula o RUC del cliente.");
        }
        String formaPago = req.formaPago() == null || req.formaPago().isBlank() ? "20" : req.formaPago().trim();

        int secuencial = siguienteSecuencial();
        String clave = SriComprobante.claveAcceso(req.fechaEmision(), e.getRuc().trim(), props().getAmbiente(),
                e.getEstablecimiento(), e.getPuntoEmision(), secuencial, String.format("%08d", random.nextInt(100_000_000)));

        FacturaEntity f = new FacturaEntity();
        f.setId(UUID.randomUUID().toString());
        f.setAmbiente(props().getAmbiente());
        f.setSecuencial(secuencial);
        f.setNumero(numero(secuencial));
        f.setClaveAcceso(clave);
        f.setEstado("CREADA");
        f.setFechaEmision(req.fechaEmision());
        f.setOrderId(blankToNull(req.orderId()));
        f.setOrderCode(blankToNull(req.orderCode()));
        f.setClienteIdentificacion(c.identificacion());
        f.setClienteNombre(c.razonSocial());
        f.setClienteEmail(blankToNull(c.email()));
        f.setClienteTelefono(blankToNull(c.telefono()));
        f.setSubtotal(t.subtotal());
        f.setIva(t.iva());
        f.setTotal(t.total());
        f.setDatosJson(objectMapper.writeValueAsString(Map.of(
                "comprador", c, "detalles", lineas, "formaPago", formaPago)));
        f.setCreatedAt(Instant.now());
        f.setCreatedBy(actor);
        repository.save(f);

        try {
            String xml = firma.firmar(SriComprobante.facturaXml(e, props().getAmbiente(), clave, secuencial,
                    req.fechaEmision(), formaPago, c, lineas));
            f.setXmlFirmado(xml);
            SriWebService ws = new SriWebService(props().getAmbiente());
            SriWebService.Respuesta r = ws.enviar(xml);
            boolean yaRecibida = r.mensajes().stream().anyMatch(m -> m.startsWith("43 "));
            if ("DEVUELTA".equals(r.estado()) && !yaRecibida) {
                f.setEstado("DEVUELTA");
                f.setMensajes(String.join("\n", r.mensajes()));
            } else {
                consultar(f, ws, 6);
            }
        } catch (Exception ex) {
            log.error("Factura {}: {}", f.getNumero(), ex.getMessage(), ex);
            f.setEstado("ERROR");
            f.setMensajes(ex.getMessage());
        }
        repository.save(f);
        if ("AUTORIZADO".equals(f.getEstado())) {
            for (FacturaProductoEntity p : usados) {
                p.setVeces(p.getVeces() + 1);
                p.setUpdatedAt(Instant.now());
                productos.save(p);
            }
        }
        return toDto(f);
    }

    /** Vuelve a preguntar al SRI (para facturas que quedaron en proceso). */
    public Map<String, Object> reconsultar(String id) {
        FacturaEntity f = find(id);
        if (!"AUTORIZADO".equals(f.getEstado())) {
            consultar(f, new SriWebService(f.getAmbiente()), 2);
            repository.save(f);
        }
        return toDto(f);
    }

    private void consultar(FacturaEntity f, SriWebService ws, int intentos) {
        for (int i = 0; i < intentos; i++) {
            SriWebService.Respuesta a = ws.autorizacion(f.getClaveAcceso());
            f.setEstado(a.estado() == null ? "EN PROCESO" : a.estado());
            f.setMensajes(String.join("\n", a.mensajes()));
            if ("AUTORIZADO".equals(a.estado())) {
                f.setNumeroAutorizacion(a.numeroAutorizacion());
                f.setFechaAutorizacion(a.fechaAutorizacion());
                return;
            }
            if ("NO AUTORIZADO".equals(a.estado())) return;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /** Guarda el PDF (RIDE) en el registro y lo envía al cliente con el XML autorizado. */
    public Map<String, Object> entregar(String id, String rideUrl, String pdfBase64, boolean enviarEmail) {
        FacturaEntity f = find(id);
        if (!"AUTORIZADO".equals(f.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La factura aún no está autorizada por el SRI.");
        }
        if (rideUrl != null && rideUrl.startsWith("https://")) f.setRideUrl(rideUrl);
        if (enviarEmail && f.getClienteEmail() != null && pdfBase64 != null && !pdfBase64.isBlank()) {
            String b64 = pdfBase64.contains(",") ? pdfBase64.substring(pdfBase64.indexOf(',') + 1) : pdfBase64;
            String xml = Base64.getEncoder().encodeToString(xmlAutorizado(f).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            mail.sendHtml(f.getClienteEmail(), "Tu factura " + f.getNumero() + " · " + s(props().getEmisor().getNombreComercial()).trim(),
                    emailHtml(f), List.of(
                            new ResendEmailService.Attachment("Factura-" + f.getNumero() + ".pdf", b64),
                            new ResendEmailService.Attachment("Factura-" + f.getNumero() + ".xml", xml)));
            f.setEmailEnviadoAt(Instant.now());
        }
        repository.save(f);
        return toDto(f);
    }

    public String xmlAutorizado(String id) {
        return xmlAutorizado(find(id));
    }

    private String xmlAutorizado(FacturaEntity f) {
        String comp = f.getXmlFirmado() == null ? "" : f.getXmlFirmado().replaceFirst("^<\\?xml[^>]*\\?>", "");
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<autorizacion>"
                + "<estado>" + s(f.getEstado()) + "</estado>"
                + "<numeroAutorizacion>" + s(f.getNumeroAutorizacion()) + "</numeroAutorizacion>"
                + "<fechaAutorizacion>" + s(f.getFechaAutorizacion()) + "</fechaAutorizacion>"
                + "<ambiente>" + ("2".equals(f.getAmbiente()) ? "PRODUCCIÓN" : "PRUEBAS") + "</ambiente>"
                + "<comprobante><![CDATA[" + comp + "]]></comprobante></autorizacion>";
    }

    String emailHtml(FacturaEntity f) {
        String negocio = esc(s(props().getEmisor().getNombreComercial()).isBlank()
                ? props().getEmisor().getRazonSocial() : props().getEmisor().getNombreComercial());
        return """
                <div style="font-family:Arial,Helvetica,sans-serif;background:#f4f7fa;padding:24px 12px">
                  <table role="presentation" width="100%%" style="max-width:560px;margin:0 auto;background:#ffffff;border-radius:16px;overflow:hidden">
                    <tr><td style="background:#1a2744;color:#ffffff;padding:22px 26px">
                      <div style="font-size:12px;letter-spacing:.1em;text-transform:uppercase;color:#5fe0e8">Factura electrónica</div>
                      <div style="font-size:22px;font-weight:bold;margin-top:4px">%s</div>
                    </td></tr>
                    <tr><td style="padding:24px 26px;color:#0f172a;font-size:15px;line-height:1.6">
                      <p style="margin:0 0 12px">Hola <strong>%s</strong>,</p>
                      <p style="margin:0 0 12px">¡Muchas gracias por tu compra! 🎁 Adjuntamos tu factura electrónica autorizada por el SRI.</p>
                      <table role="presentation" width="100%%" style="background:#f0fbfc;border-radius:12px;padding:14px 16px;font-size:14px">
                        <tr><td>N.º de factura</td><td align="right"><strong>%s</strong></td></tr>
                        <tr><td>Fecha</td><td align="right">%s</td></tr>
                        <tr><td>Total</td><td align="right"><strong>$%s</strong></td></tr>
                      </table>
                      <p style="margin:14px 0 0;font-size:13px;color:#475569">Encontrarás el PDF (RIDE) y el archivo XML adjuntos. Guárdalos para tu contabilidad.</p>
                      <p style="margin:14px 0 0">Esperamos verte pronto,<br><strong>%s</strong></p>
                    </td></tr>
                  </table>
                </div>""".formatted(negocio, esc(f.getClienteNombre()), f.getNumero(), f.getFechaEmision(),
                f.getTotal().toPlainString(), negocio);
    }

    private SriComprobante.Comprador comprador(EmitirFacturaAdminRequest.CompradorDto c) {
        String tipo = c.tipoIdentificacion().trim();
        String id = c.identificacion().replaceAll("\\s", "");
        String nombre = c.razonSocial().trim();
        switch (tipo) {
            case "05" -> { if (!id.matches("\\d{10}")) bad("La cédula debe tener 10 dígitos."); }
            case "04" -> { if (!id.matches("\\d{10}001")) bad("El RUC debe tener 13 dígitos y terminar en 001."); }
            case "07" -> { id = CONSUMIDOR_FINAL; nombre = "CONSUMIDOR FINAL"; }
            default -> { if (id.isBlank()) bad("Falta la identificación del cliente."); }
        }
        return new SriComprobante.Comprador(tipo, id, nombre.toUpperCase(Locale.ROOT), blankToNull(c.direccion()),
                blankToNull(c.telefono()), blankToNull(c.email()));
    }

    private List<SriComprobante.Linea> lineas(List<EmitirFacturaAdminRequest.DetalleLineaDto> detalles,
                                              List<FacturaProductoEntity> usados) {
        List<SriComprobante.Linea> out = new ArrayList<>();
        for (var d : detalles) {
            String codigo;
            if (d.codigoPrincipal() == null || d.codigoPrincipal().isBlank()) {
                FacturaProductoEntity p = productoFueraDeCatalogo(d.descripcion(), d.precioUnitario());
                usados.add(p);
                codigo = p.getCodigo();
            } else {
                codigo = d.codigoPrincipal().trim();
            }
            var l = new SriComprobante.Linea(codigo.length() > 25 ? codigo.substring(0, 25) : codigo,
                    d.descripcion().trim(), d.cantidad(), d.precioUnitario(),
                    d.descuento() == null ? BigDecimal.ZERO : d.descuento());
            if (l.base().signum() < 0) bad("El descuento no puede ser mayor que el valor del ítem.");
            out.add(l);
        }
        return out;
    }

    /**
     * Producto sin código: si ya se facturó (misma descripción normalizada) se reutiliza su código;
     * si no, se registra con el siguiente FX-0001, FX-0002… (nunca choca con los DG- del catálogo).
     */
    private FacturaProductoEntity productoFueraDeCatalogo(String descripcion, BigDecimal precio) {
        String clave = clave(descripcion);
        FacturaProductoEntity p = productos.findByClave(clave).orElse(null);
        if (p == null) {
            p = new FacturaProductoEntity();
            p.setCodigo("TMP-" + UUID.randomUUID().toString().substring(0, 18));
            p.setClave(clave);
            p.setDescripcion(descripcion.trim());
            p.setVeces(0);
            p.setCreatedAt(Instant.now());
            p.setUpdatedAt(Instant.now());
            p = productos.save(p);
            p.setCodigo(String.format("FX-%04d", p.getId()));
        }
        p.setUltimoPrecio(precio);
        return productos.save(p);
    }

    public List<Map<String, Object>> buscarProductos(String q) {
        return productos.findTop10ByClaveContainingOrderByVecesDesc(clave(q == null ? "" : q)).stream()
                .map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("codigo", p.getCodigo());
                    m.put("descripcion", p.getDescripcion());
                    m.put("ultimoPrecio", p.getUltimoPrecio());
                    m.put("veces", p.getVeces());
                    return m;
                })
                .toList();
    }

    /** Descripción normalizada para reconocer el mismo producto: "Caja Corazón · 25×25×6" → "CAJA-CORAZON-25-25-6". */
    static String clave(String descripcion) {
        String c = java.text.Normalizer.normalize(descripcion, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("(?<=\\d)\\s*[xX×*]\\s*(?=\\d)", "-")
                .toUpperCase(Locale.ROOT)
                .replaceAll("\\bCM\\b", " ")
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return c.length() > 300 ? c.substring(0, 300) : c;
    }

    private int siguienteSecuencial() {
        return Math.max(repository.maxSecuencial(props().getAmbiente()) + 1, props().getSecuencialInicial());
    }

    private String numero(int secuencial) {
        var e = props().getEmisor();
        return s(e.getEstablecimiento()) + "-" + s(e.getPuntoEmision()) + "-" + String.format("%09d", secuencial);
    }

    private FacturaEntity find(String id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada"));
    }

    private Map<String, Object> toDto(FacturaEntity f) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", f.getId());
        m.put("numero", f.getNumero());
        m.put("claveAcceso", f.getClaveAcceso());
        m.put("ambiente", f.getAmbiente());
        m.put("estado", f.getEstado());
        m.put("fechaEmision", f.getFechaEmision());
        m.put("numeroAutorizacion", f.getNumeroAutorizacion());
        m.put("fechaAutorizacion", f.getFechaAutorizacion());
        m.put("mensajes", f.getMensajes() == null || f.getMensajes().isBlank() ? List.of() : List.of(f.getMensajes().split("\n")));
        m.put("orderId", f.getOrderId());
        m.put("orderCode", f.getOrderCode());
        m.put("clienteNombre", f.getClienteNombre());
        m.put("clienteIdentificacion", f.getClienteIdentificacion());
        m.put("clienteEmail", f.getClienteEmail());
        m.put("clienteTelefono", f.getClienteTelefono());
        m.put("subtotal", f.getSubtotal());
        m.put("iva", f.getIva());
        m.put("total", f.getTotal());
        m.put("datos", objectMapper.readTree(f.getDatosJson()));
        m.put("rideUrl", f.getRideUrl());
        m.put("emailEnviadoAt", f.getEmailEnviadoAt());
        m.put("createdAt", f.getCreatedAt());
        return m;
    }

    private static void bad(String msg) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private static String s(String v) {
        return v == null ? "" : v.trim();
    }

    private static String blankToNull(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }

    private static String esc(String v) {
        return s(v).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
