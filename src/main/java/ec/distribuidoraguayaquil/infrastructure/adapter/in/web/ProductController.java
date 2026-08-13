package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.CatalogQueryService;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.catalog.ProductCardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Tarjetas de producto del storefront. Cada tarjeta es una variante del catálogo nuevo,
 * por lo que el alta/baja/modificación se hace en /api/admin/catalog/variantes.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final String CRUD_MOVED =
            "El CRUD de productos se gestiona en /api/admin/catalog/variantes (y /precios, /variante-imagenes)";

    private final CatalogQueryService catalogQueryService;

    @GetMapping
    public List<ProductCardDto> list(
            @RequestParam(defaultValue = "false") boolean top,
            @RequestParam(required = false) String design,
            @RequestParam(required = false) String idea) {
        return catalogQueryService.listProductCards(top, design, idea, false);
    }

    @GetMapping("/admin/all")
    public List<ProductCardDto> listAllAdmin(
            @RequestParam(required = false) String design,
            @RequestParam(required = false) String idea) {
        return catalogQueryService.listProductCards(false, design, idea, true);
    }

    @GetMapping("/{ref}")
    public ProductCardDto byRef(@PathVariable String ref) {
        return catalogQueryService.getProductCardBySku(ref);
    }

    @PostMapping
    public void create() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, CRUD_MOVED);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, CRUD_MOVED);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, CRUD_MOVED);
    }
}
