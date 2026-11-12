package ec.distribuidoraguayaquil.application.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import ec.distribuidoraguayaquil.infrastructure.config.GcsConfig.GcsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Borra del bucket los PDF de una cotización / pedido.
 * Los PDF se suben a {@code <prefix>/cotizaciones/AAAA/MM/<nombre>-<uuid>.pdf} y el nombre empieza con el código
 * ({@code cot-1023-…}, {@code ped-1017-…} o {@code cotizacion-ped-1017-…}). Nunca hace fallar el borrado del registro.
 */
@Service
public class QuotePdfStorageService {

    private static final Logger log = LoggerFactory.getLogger(QuotePdfStorageService.class);

    private final GcsProperties gcs;
    private final Storage storage;

    public QuotePdfStorageService(GcsProperties gcs, Storage storage) {
        this.gcs = gcs;
        this.storage = storage;
    }

    /** Mismo saneado que usa la subida ({@code ProductImageUploadService.sanitizeBaseName}). */
    static String sanitize(String s) {
        return s.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("(^-|-$)", "");
    }

    /** ¿El archivo pertenece a este código? (con guion final: "cot-1" no borra "cot-10…"). */
    static boolean matchesCode(String fileName, String code) {
        String c = sanitize(code);
        if (c.length() < 3) {
            return false;
        }
        String f = fileName.toLowerCase(Locale.ROOT);
        return f.startsWith(c + "-") || f.startsWith("cotizacion-" + c + "-");
    }

    /** Ruta del objeto si la URL es de nuestro bucket; si no, null. */
    String objectPathOf(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        String u = url.trim().split("[?#]")[0];
        for (String base : new String[] {gcs.publicBase() + "/", "https://storage.googleapis.com/" + gcs.getBucketName() + "/"}) {
            if (u.startsWith(base) && u.length() > base.length()) {
                return java.net.URLDecoder.decode(u.substring(base.length()), java.nio.charset.StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    /**
     * Borra los PDF del código (búsqueda en la carpeta de cotizaciones) y las URLs exactas indicadas.
     * @return cuántos archivos se borraron
     */
    public int deleteForCode(String code, Collection<String> exactUrls) {
        if (!gcs.isConfigured()) {
            return 0;
        }
        Set<String> paths = new LinkedHashSet<>();
        if (exactUrls != null) {
            for (String url : exactUrls) {
                String p = objectPathOf(url);
                if (p != null && p.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
                    paths.add(p);
                }
            }
        }
        if (code != null && !code.isBlank()) {
            try {
                String prefix = gcs.getUploadPrefix().replaceAll("/$", "") + "/cotizaciones/";
                for (Blob b : storage.list(gcs.getBucketName(), Storage.BlobListOption.prefix(prefix)).iterateAll()) {
                    String name = b.getName();
                    String file = name.substring(name.lastIndexOf('/') + 1);
                    if (matchesCode(file, code)) {
                        paths.add(name);
                    }
                }
            } catch (Exception e) {
                log.warn("No se pudo listar PDFs de {}: {}", code, e.getMessage());
            }
        }
        int deleted = 0;
        for (String p : paths) {
            try {
                if (storage.delete(BlobId.of(gcs.getBucketName(), p))) {
                    deleted++;
                }
            } catch (Exception e) {
                log.warn("No se pudo borrar {}: {}", p, e.getMessage());
            }
        }
        log.info("PDFs borrados del bucket para {}: {}", code, deleted);
        return deleted;
    }
}
