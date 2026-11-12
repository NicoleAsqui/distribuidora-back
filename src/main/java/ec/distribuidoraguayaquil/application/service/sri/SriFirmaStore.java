package ec.distribuidoraguayaquil.application.service.sri;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.SriFirmaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.SriFirmaJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Guarda la firma electrónica (.p12) cifrada; su clave nunca se guarda: se escribe al emitir,
 * se usa en memoria y se descarta. Tras varios intentos fallidos se bloquea un tiempo.
 */
@Service
public class SriFirmaStore {

    private static final int ID = 1;
    private static final int MAX_FALLOS = 5;
    private static final Duration BLOQUEO = Duration.ofMinutes(15);

    private final SriFirmaJpaRepository repository;
    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();
    private int fallos;
    private Instant bloqueadaHasta = Instant.EPOCH;

    public SriFirmaStore(SriFirmaJpaRepository repository,
                         @Value("${app.sri.firma-key:}") String firmaKey,
                         @Value("${app.admin.token-secret:distribuidora-dev-secret-change-me}") String tokenSecret) {
        this.repository = repository;
        String material = firmaKey == null || firmaKey.isBlank() ? tokenSecret : firmaKey;
        try {
            byte[] k = MessageDigest.getInstance("SHA-256").digest(("sri-firma|" + material).getBytes(StandardCharsets.UTF_8));
            this.key = new SecretKeySpec(k, "AES");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<Map<String, Object>> info() {
        return repository.findById(ID).map(f -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("titular", f.getTitular());
            m.put("validaHasta", f.getValidaHasta());
            m.put("subidaAt", f.getSubidaAt());
            m.put("vencida", f.getValidaHasta() != null && f.getValidaHasta().isBefore(Instant.now()));
            return m;
        });
    }

    /** Verifica que la firma abra con su clave y la guarda cifrada (reemplaza la anterior). */
    public synchronized Map<String, Object> guardar(byte[] p12, char[] clave, String actor) {
        SriFirma firma = abrirBytes(p12, clave);
        X509Certificate c = firma.certificado();
        SriFirmaEntity e = repository.findById(ID).orElseGet(SriFirmaEntity::new);
        e.setId(ID);
        e.setP12Cifrado(cifrar(p12));
        e.setTitular(titular(c));
        e.setValidaDesde(c.getNotBefore().toInstant());
        e.setValidaHasta(c.getNotAfter().toInstant());
        e.setSubidaAt(Instant.now());
        e.setSubidaPor(actor);
        repository.save(e);
        return info().orElseThrow();
    }

    /** Abre la firma guardada con la clave escrita por el admin. */
    public synchronized SriFirma abrir(char[] clave) {
        SriFirmaEntity e = repository.findById(ID).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.CONFLICT, "Primero sube la firma electrónica (.p12)."));
        if (e.getValidaHasta() != null && e.getValidaHasta().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La firma electrónica está vencida. Sube la nueva.");
        }
        return abrirBytes(descifrar(e.getP12Cifrado()), clave);
    }

    private SriFirma abrirBytes(byte[] p12, char[] clave) {
        Instant now = Instant.now();
        if (now.isBefore(bloqueadaHasta)) {
            long min = Math.max(1, Duration.between(now, bloqueadaHasta).toMinutes());
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Demasiados intentos con clave incorrecta. Espera " + min + " min.");
        }
        if (clave == null || clave.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escribe la clave de la firma electrónica.");
        }
        try {
            SriFirma f = new SriFirma(p12, clave);
            fallos = 0;
            return f;
        } catch (IllegalStateException ex) {
            fallos++;
            if (fallos >= MAX_FALLOS) {
                fallos = 0;
                bloqueadaHasta = now.plus(BLOQUEO);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "Clave incorrecta. Por seguridad se bloqueó la firma por 15 min.");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Clave de la firma incorrecta. Te quedan " + (MAX_FALLOS - fallos) + " intentos.");
        }
    }

    private static String titular(X509Certificate c) {
        String dn = c.getSubjectX500Principal().getName();
        for (String part : dn.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")) {
            if (part.trim().startsWith("CN=")) return part.trim().substring(3).replace("\"", "");
        }
        return dn;
    }

    String cifrar(byte[] data) {
        try {
            byte[] iv = new byte[12];
            random.nextBytes(iv);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] ct = c.doFinal(data);
            return Base64.getEncoder().encodeToString(ByteBuffer.allocate(iv.length + ct.length).put(iv).put(ct).array());
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cifrar la firma", e);
        }
    }

    byte[] descifrar(String b64) {
        try {
            byte[] all = Base64.getDecoder().decode(b64);
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, all, 0, 12));
            return c.doFinal(all, 12, all.length - 12);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se pudo leer la firma guardada (¿cambió ADMIN_TOKEN_SECRET?). Súbela de nuevo.");
        }
    }
}
