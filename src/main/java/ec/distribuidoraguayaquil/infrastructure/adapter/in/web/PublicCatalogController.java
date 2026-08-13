package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.CatalogQueryService;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.IdeaDto;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PapelForroEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VinilEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/** Lecturas públicas del catálogo (sin autenticación). */
@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class PublicCatalogController {

    private final CatalogQueryService catalogQueryService;

    @GetMapping("/disenos")
    public ResponseEntity<List<DisenoEntity>> disenos() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES).cachePublic())
                .body(catalogQueryService.listDisenosActivos());
    }

    @GetMapping("/ideas")
    public ResponseEntity<List<IdeaDto>> ideas() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(2, TimeUnit.MINUTES).cachePublic())
                .body(catalogQueryService.listIdeasActivas());
    }

    @GetMapping("/ideas/{slug}")
    public IdeaDto idea(@PathVariable String slug) {
        return catalogQueryService.getIdeaBySlug(slug);
    }


    /**
     * Papeles de forro (texturas). Solo para cartón forrado en Personaliza.
     */
    @GetMapping("/papeles-forro")
    public List<PapelForroEntity> papelesForro() {
        return catalogQueryService.listPapelesForroActivos();
    }

    /**
     * Vinilos de forro. Opcional {@code ?tipo=mate|brillante|impreso}.
     * Solo para cartón forrado en Personaliza.
     */
    @GetMapping("/viniles")
    public List<VinilEntity> viniles(@RequestParam(required = false) String tipo) {
        return catalogQueryService.listVinilesActivos(tipo);
    }

    /** Nombres de diseño activos; el frontend los usa como filtro de categoría. */
    @GetMapping("/product-categories")
    public List<String> productCategories() {
        return catalogQueryService.listCategorias();
    }

}
