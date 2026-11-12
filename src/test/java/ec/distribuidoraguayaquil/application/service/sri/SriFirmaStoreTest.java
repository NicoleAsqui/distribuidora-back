package ec.distribuidoraguayaquil.application.service.sri;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.SriFirmaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.SriFirmaJpaRepository;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SriFirmaStoreTest {

    static byte[] p12(String clave) throws Exception {
        var g = KeyPairGenerator.getInstance("RSA");
        g.initialize(2048);
        var kp = g.generateKeyPair();
        X500Name dn = new X500Name("CN=ASQUI MANZABA NICOLE KATHERINE,O=Prueba");
        X509Certificate cert = new JcaX509CertificateConverter().getCertificate(new JcaX509v3CertificateBuilder(
                dn, BigInteger.TWO, new Date(), new Date(System.currentTimeMillis() + 86_400_000L), dn, kp.getPublic())
                .build(new JcaContentSignerBuilder("SHA256withRSA").build(kp.getPrivate())));
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(null, null);
        ks.setKeyEntry("firma", kp.getPrivate(), clave.toCharArray(), new X509Certificate[] {cert});
        var out = new ByteArrayOutputStream();
        ks.store(out, clave.toCharArray());
        return out.toByteArray();
    }

    @Test
    void guardaCifradaPideClaveYBloqueaTrasIntentosFallidos() throws Exception {
        AtomicReference<SriFirmaEntity> db = new AtomicReference<>();
        SriFirmaJpaRepository repo = mock(SriFirmaJpaRepository.class);
        when(repo.findById(any())).thenAnswer(i -> Optional.ofNullable(db.get()));
        when(repo.save(any())).thenAnswer(i -> {
            db.set(i.getArgument(0));
            return i.getArgument(0);
        });
        SriFirmaStore store = new SriFirmaStore(repo, "", "secreto-de-prueba");
        byte[] p12 = p12("MiClave123");

        var info = store.guardar(p12, "MiClave123".toCharArray(), "jeymy");
        assertEquals("ASQUI MANZABA NICOLE KATHERINE", info.get("titular"));
        assertFalse(db.get().getP12Cifrado().contains(java.util.Base64.getEncoder().encodeToString(p12).substring(0, 40)));

        assertNotNull(store.abrir("MiClave123".toCharArray()).certificado());

        for (int i = 1; i <= 4; i++) {
            var ex = assertThrows(ResponseStatusException.class, () -> store.abrir("mala".toCharArray()));
            assertEquals(400, ex.getStatusCode().value());
        }
        var bloqueo = assertThrows(ResponseStatusException.class, () -> store.abrir("mala".toCharArray()));
        assertEquals(429, bloqueo.getStatusCode().value());
        // Bloqueada: ni la clave correcta abre hasta que pase el tiempo.
        var ex = assertThrows(ResponseStatusException.class, () -> store.abrir("MiClave123".toCharArray()));
        assertEquals(429, ex.getStatusCode().value());

        // Otra llave de servidor no puede leer la firma guardada.
        SriFirmaStore otra = new SriFirmaStore(repo, "", "otro-secreto");
        assertTrue(assertThrows(ResponseStatusException.class, () -> otra.abrir("MiClave123".toCharArray()))
                .getReason().contains("Súbela de nuevo"));
    }
}
