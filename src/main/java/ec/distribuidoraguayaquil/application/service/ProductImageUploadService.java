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
    private static final int THUMB_MAX = 280;
    private static final double FULL_QUALITY = 0.82;
    /** Miniatura: calidad mínima para listados (menos peso). */
    private static final double THUMB_QUALITY = 0.45;
    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
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
        String contentType = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se permiten imágenes JPEG, PNG, WebP o GIF");
        }

        try {
            byte[] original = file.getBytes();
            byte[] fullJpeg = resizeJpeg(original, FULL_MAX, FULL_QUALITY);
            byte[] thumbJpeg = resizeJpeg(original, THUMB_MAX, THUMB_QUALITY);

            String baseName = sanitizeBaseName(file.getOriginalFilename());
            LocalDate now = LocalDate.now();
            String root = gcsProperties.getUploadPrefix().replaceAll("/$", "");
            if (folderOverride != null && !folderOverride.isBlank()) {
                root = root + "/" + folderOverride.trim().replaceAll("^/+|/+$", "");
            }
            String prefix = root + "/" + now.getYear() + "/" + String.format("%02d", now.getMonthValue());
            String objectPathFull = prefix + "/" + baseName + ".jpg";
            String objectPathThumb = prefix + "/" + baseName + "-thumb.jpg";

            String image = putObject(objectPathFull, fullJpeg);
            String imageThumb = putObject(objectPathThumb, thumbJpeg);

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

    private String putObject(String objectPath, byte[] bytes) {
        return putObject(objectPath, bytes, "image/jpeg");
    }

    private String putObject(String objectPath, byte[] bytes, String contentType) {
        BlobInfo info = BlobInfo.newBuilder(gcsProperties.getBucketName(), objectPath)
                .setContentType(contentType)
                .setCacheControl(gcsProperties.getCacheControl())
                .build();
        storage.create(info, bytes);
        return gcsProperties.publicBase() + "/" + objectPath;
    }

    private static byte[] resizeJpeg(byte[] input, int maxSide, double quality) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(input))
                .size(maxSide, maxSide)
                .keepAspectRatio(true)
                .outputFormat("jpg")
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
