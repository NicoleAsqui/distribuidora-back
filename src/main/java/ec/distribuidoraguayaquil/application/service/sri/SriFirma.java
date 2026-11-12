package ec.distribuidoraguayaquil.application.service.sri;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.production.BasicSignatureOptions;
import xades4j.production.DataObjectReference;
import xades4j.production.SignedDataObjects;
import xades4j.production.SigningCertificateMode;
import xades4j.properties.DataObjectFormatProperty;
import xades4j.production.SignatureAlgorithms;
import xades4j.production.XadesBesSigningProfile;
import xades4j.production.XadesSigner;
import xades4j.providers.impl.DirectKeyingDataProvider;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;

/** Firma XAdES-BES (RSA-SHA1, como pide la ficha técnica del SRI) con la firma electrónica .p12. */
public final class SriFirma {

    private static final String RSA_SHA1 = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    private static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";

    private final XadesSigner signer;
    private final X509Certificate certificado;

    public SriFirma(byte[] p12, char[] password) {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new ByteArrayInputStream(p12), password);
            PrivateKey key = null;
            X509Certificate cert = null;
            for (String alias : Collections.list(ks.aliases())) {
                if (!ks.isKeyEntry(alias)) continue;
                X509Certificate c = (X509Certificate) ks.getCertificate(alias);
                boolean[] usage = c.getKeyUsage();
                boolean firma = usage == null || (usage.length > 0 && usage[0]);
                if (key == null || firma) {
                    key = (PrivateKey) ks.getKey(alias, password);
                    cert = c;
                    if (firma) break;
                }
            }
            if (key == null) {
                throw new IllegalStateException("La firma .p12 no tiene clave privada");
            }
            this.certificado = cert;
            this.signer = new XadesBesSigningProfile(new DirectKeyingDataProvider(cert, key))
                    .withSignatureAlgorithms(new SignatureAlgorithms()
                            .withSignatureAlgorithm("RSA", RSA_SHA1)
                            .withDigestAlgorithmForDataObjectReferences(SHA1)
                            .withDigestAlgorithmForReferenceProperties(SHA1))
                    .withBasicSignatureOptions(new BasicSignatureOptions()
                            .includeSigningCertificate(SigningCertificateMode.SIGNING_CERTIFICATE)
                            .includePublicKey(true)
                            .signKeyInfo(true))
                    .newSigner();
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo abrir la firma .p12 (¿clave correcta?): " + e.getMessage(), e);
        }
    }

    public X509Certificate certificado() {
        return certificado;
    }

    /** Firma el documento (raíz con id="comprobante") y lo devuelve como texto UTF-8. */
    public String firmar(Document doc) {
        try {
            // El SRI exige que la firma referencie el nodo raíz por su id: URI="#comprobante".
            Element root = doc.getDocumentElement();
            root.setIdAttribute("id", true);
            var comprobante = new DataObjectReference("#" + root.getAttribute("id"))
                    .withTransform(new EnvelopedSignatureTransform())
                    .withDataObjectFormat(new DataObjectFormatProperty("text/xml").withDescription("contenido comprobante"));
            signer.sign(new SignedDataObjects(comprobante), root);
            var tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StringWriter out = new StringWriter();
            tf.transform(new DOMSource(doc), new StreamResult(out));
            return out.toString();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar la factura: " + e.getMessage(), e);
        }
    }
}
