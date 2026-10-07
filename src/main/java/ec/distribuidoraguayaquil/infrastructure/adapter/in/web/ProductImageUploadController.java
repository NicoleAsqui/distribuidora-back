package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.ProductImageUploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
public class ProductImageUploadController {

    private final ProductImageUploadService uploadService;

    public ProductImageUploadController(ProductImageUploadService uploadService) {
        this.uploadService = uploadService;
    }

    /**
     * Multipart campo {@code image}. Genera JPEG detalle (~1200) + miniatura (~380) y sube a GCS.
     * Respuesta: {@code { image, imageThumb, objectPathFull, objectPathThumb }}.
     */
    @PostMapping("/product-images")
    public Map<String, String> uploadProductImage(@RequestParam("image") MultipartFile image) {
        return uploadService.upload(image);
    }

    /**
     * Proxy de imágenes del bucket GCS (admin) para embeber fotos en PDF sin CORS del navegador.
     */
    @GetMapping("/image-proxy")
    public ResponseEntity<byte[]> imageProxy(@RequestParam("url") String url) {
        ProductImageUploadService.ProxiedImage img = uploadService.fetchAllowedImage(url);
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(img.contentType());
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600")
                .body(img.bytes());
    }

    /**
     * Arte del cliente para vinil impreso / impresión en Personaliza (público).
     * Solo imágenes JPEG/PNG/WebP/GIF de buena calidad.
     */
    @PostMapping("/quote-art")
    public Map<String, String> uploadQuoteArt(@RequestParam("image") MultipartFile image) {
        return uploadService.upload(image, "quote-art");
    }

    /** Texturas de papel forro (admin). */
    @PostMapping("/forro-textures")
    public Map<String, String> uploadForroTexture(@RequestParam("image") MultipartFile image) {
        return uploadService.upload(image, "forro-textures");
    }

    /**
     * PDF de cotización del checkout (público). Multipart campo {@code file}.
     * Respuesta: {@code { url, objectPath }}.
     */
    @PostMapping("/quote-pdf")
    public Map<String, String> uploadQuotePdf(@RequestParam("file") MultipartFile file) {
        return uploadService.uploadPdf(file, "cotizaciones");
    }

    /**
     * Video del diseño (admin) para reproducir en la web. Multipart campo {@code file}.
     * Respuesta: {@code { url, objectPath }}.
     */
    @PostMapping("/diseno-videos")
    public Map<String, String> uploadDisenoVideo(@RequestParam("file") MultipartFile file) {
        return uploadService.uploadVideo(file, "diseno-videos");
    }
}
