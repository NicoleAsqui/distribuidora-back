package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.ProductImageUploadService;
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
}
