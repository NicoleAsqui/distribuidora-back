package ec.distribuidoraguayaquil.application.service.sri;

import ec.distribuidoraguayaquil.infrastructure.config.SriFacturacionConfig.SriFacturacionProperties.Emisor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/** Factura electrónica SRI (versión 2.1.0) y su clave de acceso. */
public final class SriComprobante {

    public static final BigDecimal IVA_TARIFA = new BigDecimal("15");
    private static final BigDecimal IVA_FACTOR = new BigDecimal("0.15");
    /** Código SRI del IVA 15 %. */
    private static final String IVA_CODIGO_PORCENTAJE = "4";

    private SriComprobante() {}

    public record Comprador(String tipoIdentificacion, String identificacion, String razonSocial,
                            String direccion, String telefono, String email) {}

    public record Linea(String codigo, String descripcion, BigDecimal cantidad, BigDecimal precioUnitario,
                        BigDecimal descuento) {
        public BigDecimal base() {
            BigDecimal d = descuento == null ? BigDecimal.ZERO : descuento;
            return cantidad.multiply(precioUnitario).subtract(d).setScale(2, RoundingMode.HALF_UP);
        }

        public BigDecimal iva() {
            return base().multiply(IVA_FACTOR).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public record Totales(BigDecimal subtotal, BigDecimal descuento, BigDecimal iva, BigDecimal total) {}

    public static Totales totales(List<Linea> lineas) {
        BigDecimal sub = BigDecimal.ZERO;
        BigDecimal desc = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        for (Linea l : lineas) {
            sub = sub.add(l.base());
            desc = desc.add(l.descuento() == null ? BigDecimal.ZERO : l.descuento());
            iva = iva.add(l.iva());
        }
        return new Totales(sub.setScale(2, RoundingMode.HALF_UP), desc.setScale(2, RoundingMode.HALF_UP),
                iva.setScale(2, RoundingMode.HALF_UP), sub.add(iva).setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Clave de acceso de 49 dígitos: fecha(8) + tipo(2) + RUC(13) + ambiente(1) + serie(6) + secuencial(9)
     * + código numérico(8) + tipo de emisión(1) + dígito verificador módulo 11.
     */
    public static String claveAcceso(String fechaDdMmYyyy, String ruc, String ambiente, String estab, String pto,
                                     int secuencial, String codigoNumerico) {
        String base = fechaDdMmYyyy.replace("/", "") + "01" + ruc + ambiente + estab + pto
                + String.format("%09d", secuencial) + codigoNumerico + "1";
        if (base.length() != 48 || !base.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("Clave de acceso inválida: " + base);
        }
        return base + digitoModulo11(base);
    }

    static int digitoModulo11(String digits) {
        int sum = 0;
        int factor = 2;
        for (int i = digits.length() - 1; i >= 0; i--) {
            sum += (digits.charAt(i) - '0') * factor;
            factor = factor == 7 ? 2 : factor + 1;
        }
        int d = 11 - (sum % 11);
        if (d == 11) return 0;
        if (d == 10) return 1;
        return d;
    }

    public static Document facturaXml(Emisor e, String ambiente, String claveAcceso, int secuencial,
                                      String fechaEmision, String formaPago, Comprador c, List<Linea> lineas) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            Document doc = f.newDocumentBuilder().newDocument();
            Element root = doc.createElement("factura");
            root.setAttribute("id", "comprobante");
            root.setAttribute("version", "2.1.0");
            root.setIdAttribute("id", true);
            doc.appendChild(root);

            Element it = add(doc, root, "infoTributaria");
            text(doc, it, "ambiente", ambiente);
            text(doc, it, "tipoEmision", "1");
            text(doc, it, "razonSocial", e.getRazonSocial());
            if (notBlank(e.getNombreComercial())) text(doc, it, "nombreComercial", e.getNombreComercial());
            text(doc, it, "ruc", e.getRuc());
            text(doc, it, "claveAcceso", claveAcceso);
            text(doc, it, "codDoc", "01");
            text(doc, it, "estab", e.getEstablecimiento());
            text(doc, it, "ptoEmi", e.getPuntoEmision());
            text(doc, it, "secuencial", String.format("%09d", secuencial));
            text(doc, it, "dirMatriz", e.getDirMatriz());
            if (notBlank(e.getContribuyenteRimpe())) text(doc, it, "contribuyenteRimpe", e.getContribuyenteRimpe());

            Totales t = totales(lineas);
            Element inf = add(doc, root, "infoFactura");
            text(doc, inf, "fechaEmision", fechaEmision);
            text(doc, inf, "dirEstablecimiento",
                    notBlank(e.getDirEstablecimiento()) ? e.getDirEstablecimiento() : e.getDirMatriz());
            text(doc, inf, "obligadoContabilidad", notBlank(e.getObligadoContabilidad()) ? e.getObligadoContabilidad() : "NO");
            text(doc, inf, "tipoIdentificacionComprador", c.tipoIdentificacion());
            text(doc, inf, "razonSocialComprador", c.razonSocial());
            text(doc, inf, "identificacionComprador", c.identificacion());
            if (notBlank(c.direccion())) text(doc, inf, "direccionComprador", c.direccion());
            text(doc, inf, "totalSinImpuestos", money(t.subtotal()));
            text(doc, inf, "totalDescuento", money(t.descuento()));
            Element tci = add(doc, inf, "totalConImpuestos");
            Element ti = add(doc, tci, "totalImpuesto");
            text(doc, ti, "codigo", "2");
            text(doc, ti, "codigoPorcentaje", IVA_CODIGO_PORCENTAJE);
            text(doc, ti, "baseImponible", money(t.subtotal()));
            text(doc, ti, "valor", money(t.iva()));
            text(doc, inf, "propina", "0.00");
            text(doc, inf, "importeTotal", money(t.total()));
            text(doc, inf, "moneda", "DOLAR");
            Element pagos = add(doc, inf, "pagos");
            Element pago = add(doc, pagos, "pago");
            text(doc, pago, "formaPago", formaPago);
            text(doc, pago, "total", money(t.total()));

            Element dets = add(doc, root, "detalles");
            for (Linea l : lineas) {
                Element d = add(doc, dets, "detalle");
                text(doc, d, "codigoPrincipal", l.codigo());
                text(doc, d, "descripcion", l.descripcion());
                text(doc, d, "cantidad", l.cantidad().setScale(2, RoundingMode.HALF_UP).toPlainString());
                BigDecimal precio = l.precioUnitario().setScale(6, RoundingMode.HALF_UP).stripTrailingZeros();
                text(doc, d, "precioUnitario", (precio.scale() < 2 ? precio.setScale(2) : precio).toPlainString());
                text(doc, d, "descuento", money(l.descuento() == null ? BigDecimal.ZERO : l.descuento()));
                text(doc, d, "precioTotalSinImpuesto", money(l.base()));
                Element imps = add(doc, d, "impuestos");
                Element imp = add(doc, imps, "impuesto");
                text(doc, imp, "codigo", "2");
                text(doc, imp, "codigoPorcentaje", IVA_CODIGO_PORCENTAJE);
                text(doc, imp, "tarifa", IVA_TARIFA.toPlainString());
                text(doc, imp, "baseImponible", money(l.base()));
                text(doc, imp, "valor", money(l.iva()));
            }

            if (notBlank(c.email()) || notBlank(c.telefono())) {
                Element ad = add(doc, root, "infoAdicional");
                if (notBlank(c.email())) campo(doc, ad, "Email", c.email());
                if (notBlank(c.telefono())) campo(doc, ad, "Telefono", c.telefono());
            }
            return doc;
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo armar el XML de la factura", ex);
        }
    }

    private static Element add(Document doc, Element parent, String name) {
        Element el = doc.createElement(name);
        parent.appendChild(el);
        return el;
    }

    private static void text(Document doc, Element parent, String name, String value) {
        add(doc, parent, name).setTextContent(value == null ? "" : value.trim());
    }

    private static void campo(Document doc, Element parent, String nombre, String value) {
        Element el = add(doc, parent, "campoAdicional");
        el.setAttribute("nombre", nombre);
        el.setTextContent(value.trim());
    }

    private static String money(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}
