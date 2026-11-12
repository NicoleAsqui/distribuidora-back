package ec.distribuidoraguayaquil.application.service.sri;

import ec.distribuidoraguayaquil.infrastructure.config.SriFacturacionConfig.SriFacturacionProperties;
import org.apache.xml.security.Init;
import org.apache.xml.security.signature.XMLSignature;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SriComprobanteTest {

    private static SriFacturacionProperties.Emisor emisor() {
        var e = new SriFacturacionProperties().getEmisor();
        e.setRuc("0953518057001");
        e.setRazonSocial("EMISOR DE PRUEBA");
        e.setNombreComercial("Distribuidora Guayaquil");
        e.setDirMatriz("Guayaquil");
        return e;
    }

    private static List<SriComprobante.Linea> lineas() {
        return List.of(
                new SriComprobante.Linea("DG-0026", "Caja base y tapa 25x25x5", new BigDecimal("50"), new BigDecimal("0.59"), BigDecimal.ZERO),
                new SriComprobante.Linea("DG-0457", "Caja desayuno 20x20x6", new BigDecimal("3"), new BigDecimal("1.7567"), new BigDecimal("0.27")));
    }

    @Test
    void claveAccesoTiene49DigitosYVerificadorModulo11() {
        String clave = SriComprobante.claveAcceso("12/11/2026", "0953518057001", "1", "001", "001", 123, "12345678");
        assertEquals(49, clave.length());
        assertTrue(clave.startsWith("12112026010953518057001100100100000012312345678"));
        assertEquals(clave.charAt(48) - '0', SriComprobante.digitoModulo11(clave.substring(0, 48)));
        // Caso conocido: 41261533 → 6 (ejemplo clásico de módulo 11)
        assertEquals(6, SriComprobante.digitoModulo11("41261533"));
        // Factura real autorizada por el SRI (002-001-000000090): mismo formato y dígito verificador 4.
        assertEquals("0411202601095351805700120020010000000906607099914",
                SriComprobante.claveAcceso("04/11/2026", "0953518057001", "2", "002", "001", 90, "66070999"));
    }

    @Test
    void totalesConIva15() {
        var t = SriComprobante.totales(lineas());
        // 50 × 0.59 = 29.50 ; 3 × 1.7567 − 0.27 = 5.00 → 34.50 ; IVA 4.43 + 0.75 = 5.18
        assertEquals(new BigDecimal("34.50"), t.subtotal());
        assertEquals(new BigDecimal("5.18"), t.iva());
        assertEquals(new BigDecimal("39.68"), t.total());
    }

    @Test
    void facturaFirmadaEsValida() throws Exception {
        KeyPairGenerator g = KeyPairGenerator.getInstance("RSA");
        g.initialize(2048);
        KeyPair kp = g.generateKeyPair();
        X500Name dn = new X500Name("CN=Prueba SRI");
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(new JcaX509v3CertificateBuilder(
                dn, BigInteger.ONE, new Date(), new Date(System.currentTimeMillis() + 86_400_000L), dn, kp.getPublic())
                .build(new JcaContentSignerBuilder("SHA256withRSA").build(kp.getPrivate())));
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null, null);
        ks.setKeyEntry("firma", kp.getPrivate(), "clave".toCharArray(), new X509Certificate[] {cert});
        ByteArrayOutputStream p12 = new ByteArrayOutputStream();
        ks.store(p12, "clave".toCharArray());

        String clave = SriComprobante.claveAcceso("12/11/2026", "0953518057001", "1", "001", "001", 1, "00000001");
        Document doc = SriComprobante.facturaXml(emisor(), "1", clave, 1, "12/11/2026", "20",
                new SriComprobante.Comprador("05", "0912345678", "ANA PEREZ", "Guayaquil", "0991234567", "ana@test.com"),
                lineas());
        String xml = new SriFirma(p12.toByteArray(), "clave".toCharArray()).firmar(doc);

        assertTrue(xml.contains("<claveAcceso>" + clave + "</claveAcceso>"));
        assertTrue(xml.contains("<importeTotal>39.68</importeTotal>"));
        assertTrue(xml.contains("xades:SignedProperties") || xml.contains("SignedProperties"));

        Init.init();
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        Document signed = f.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        signed.getDocumentElement().setIdAttribute("id", true);
        Element sigEl = (Element) signed.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature").item(0);
        var props = signed.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", "SignedProperties");
        for (int i = 0; i < props.getLength(); i++) ((Element) props.item(i)).setIdAttribute("Id", true);
        XMLSignature sig = new XMLSignature(sigEl, "");
        assertEquals(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1, sig.getSignedInfo().getSignatureMethodURI());
        assertTrue(sig.checkSignatureValue(cert));
    }
}
