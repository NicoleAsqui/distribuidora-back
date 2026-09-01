package ec.distribuidoraguayaquil.application.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import ec.distribuidoraguayaquil.infrastructure.config.GcsConfig.GcsProperties;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductImageUploadService {

    private static final int FULL_MAX = 1200;
    /** Miniatura catálogo: más grande y nítida para ver el modelo sin ir al detalle. */
    private static final int THUMB_MAX = 520;
    private static final double FULL_QUALITY = 0.86;
    private static final double THUMB_QUALITY = 0.80;
    private static final String OUTPUT_FORMAT = "webp";
    private static final String OUTPUT_EXT = ".webp";
    private static final String OUTPUT_MIME = "image/webp";
    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif", "image/pjpeg", "image/x-png"
    );

    private final GcsProperties gcsProperties;
    private final Storage storage;

    public ProductImageUploadService(GcsProperties gcsProperties, Storage storage) {
        this.gcsProperties = gcsProperties;
        this.storage = storage;
    }

    public Map<String, String> upload(MultipartFile file) {
        return upload(file, null);
    }

    /**
     * @param folderOverride subcarpeta bajo el prefix GCS (ej. {@code quote-art}, {@code forro-textures}).
     */
    public Map<String, String> upload(MultipartFile file, String folderOverride) {
        if (!gcsProperties.isConfigured()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "GCS no configurado: define GCS_BUCKET_NAME");
        }
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el campo multipart \"image\"");
        }

        try {
            byte[] original = file.getBytes();
            String contentType = resolveContentType(file, original);
            if (!ALLOWED.contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Solo se permiten imágenes JPEG, PNG, WebP o GIF (recibido: "
                                + (file.getContentType() == null ? "sin tipo" : file.getContentType()) + ")");
            }

            byte[] full = resize(original, FULL_MAX, FULL_QUALITY);
            byte[] thumb = resize(original, THUMB_MAX, THUMB_QUALITY);

            String baseName = sanitizeBaseName(file.getOriginalFilename());
            LocalDate now = LocalDate.now();
            String root = gcsProperties.getUploadPrefix().replaceAll("/$", "");
            if (folderOverride != null && !folderOverride.isBlank()) {
                root = root + "/" + folderOverride.trim().replaceAll("^/+|/+$", "");
            }
            String prefix = root + "/" + now.getYear() + "/" + String.format("%02d", now.getMonthValue());
            String objectPathFull = prefix + "/" + baseName + OUTPUT_EXT;
            String objectPathThumb = prefix + "/" + baseName + "-thumb" + OUTPUT_EXT;

            String image = putObject(objectPathFull, full, OUTPUT_MIME);
            String imageThumb = putObject(objectPathThumb, thumb, OUTPUT_MIME);

            return Map.of(
                    "image", image,
                    "imageThumb", imageThumb,
                    "objectPathFull", objectPathFull,
                    "objectPathThumb", objectPathThumb
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo procesar o subir la imagen: " + e.getMessage(), e);
        }
    }

    /**
     * Sube un PDF de cotización (checkout) a GCS. Multipart campo {@code file}.
     * Respuesta: {@code { url, objectPath }}.
     */
    public Map<String, String> uploadPdf(MultipartFile file, String folderOverride) {
        if (!gcsProperties.isConfigured()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "GCS no configurado: define GCS_BUCKET_NAME");
        }
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el campo multipart \"file\"");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        String name = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase(Locale.ROOT);
        boolean looksPdf = contentType.contains("pdf") || name.endsWith(".pdf");
        if (!looksPdf) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten archivos PDF");
        }
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length > 12 * 1024 * 1024) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PDF demasiado grande (máx. 12 MB)");
            }
            String baseName = sanitizeBaseName(file.getOriginalFilename());
            if (baseName.isBlank() || "image".equals(baseName)) {
                baseName = "cotizacion-" + UUID.randomUUID().toString().substring(0, 8);
            }
            LocalDate now = LocalDate.now();
            String root = gcsProperties.getUploadPrefix().replaceAll("/$", "");
            String folder = (folderOverride == null || folderOverride.isBlank()) ? "cotizaciones" : folderOverride.trim();
            root = root + "/" + folder.replaceAll("^/+|/+$", "");
            String objectPath = root + "/" + now.getYear() + "/" + String.format("%02d", now.getMonthValue())
                    + "/" + baseName + "-" + UUID.randomUUID().toString().substring(0, 8) + ".pdf";
            BlobInfo info = BlobInfo.newBuilder(gcsProperties.getBucketName(), objectPath)
                    .setContentType("application/pdf")
                    .setContentDisposition("inline; filename=\"" + baseName + ".pdf\"")
                    .setCacheControl(gcsProperties.getCacheControl())
                    .build();
            storage.create(info, bytes);
            String url = gcsProperties.publicBase() + "/" + objectPath;
            return Map.of("url", url, "objectPath", objectPath);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo subir el PDF: " + e.getMessage(), e);
        }
    }

    /** Content-Type del multipart, extensión o firma del archivo (Safari a veces manda vacío). */
    private static String resolveContentType(MultipartFile file, byte[] bytes) {
        String raw = file.getContentType();
        if (raw != null && !raw.isBlank()) {
            String ct = raw.toLowerCase(Locale.ROOT).split(";")[0].trim();
            if (ALLOWED.contains(ct)) {
                return ct;
            }
            if ("application/octet-stream".equals(ct)) {
                String sniffed = sniffMagic(bytes);
                if (sniffed != null) {
                    return sniffed;
                }
            }
        }
        String fromExt = fromExtension(file.getOriginalFilename());
        if (fromExt != null) {
            return fromExt;
        }
        String sniffed = sniffMagic(bytes);
        if (sniffed != null) {
            return sniffed;
        }
        return raw == null ? "" : raw.toLowerCase(Locale.ROOT);
    }

    private static String fromExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }
        String lower = filename.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        return null;
    }

    private static String sniffMagic(byte[] bytes) {
        if (bytes == null || bytes.length < 12) {
            return null;
        }
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8) {
            return "image/jpeg";
        }
        if (bytes[0] == (byte) 0x89 && bytes[1] == 0x50 && bytes[2] == 0x4E && bytes[3] == 0x47) {
            return "image/png";
        }
        if (bytes[0] == 0x47 && bytes[1] == 0x49 && bytes[2] == 0x46) {
            return "image/gif";
        }
        if (bytes[0] == 0x52 && bytes[1] == 0x49 && bytes[2] == 0x46 && bytes[3] == 0x46
                && bytes[8] == 0x57 && bytes[9] == 0x45 && bytes[10] == 0x42 && bytes[11] == 0x50) {
            return "image/webp";
        }
        return null;
    }

    private String putObject(String objectPath, byte[] bytes, String contentType) {
        BlobInfo info = BlobInfo.newBuilder(gcsProperties.getBucketName(), objectPath)
                .setContentType(contentType)
                .setCacheControl(gcsProperties.getCacheControl())
                .build();
        storage.create(info, bytes);
        return gcsProperties.publicBase() + "/" + objectPath;
    }

    private static byte[] resize(byte[] input, int maxSide, double quality) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(input))
                .size(maxSide, maxSide)
                .keepAspectRatio(true)
                .outputFormat(OUTPUT_FORMAT)
                .outputQuality(quality)
                .toOutputStream(out);
        return out.toByteArray();
    }

    private static String sanitizeBaseName(String original) {
        String name = original == null || original.isBlank() ? "image" : original;
        int dot = name.lastIndexOf('.');
        if (dot > 0) name = name.substring(0, dot);
        name = name.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("(^-|-$)", "");
        if (name.isBlank()) name = "image";
        return name + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
