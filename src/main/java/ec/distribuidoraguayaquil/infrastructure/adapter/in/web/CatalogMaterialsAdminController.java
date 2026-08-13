package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.NewCatalogAdminService;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.ColorEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.GramajeEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MaterialColorEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MaterialEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MaterialImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PapelForroEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.TipoMaterialEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VinilEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Administración de materiales, colores y gramajes del catálogo nuevo. */
@RestController
@RequestMapping("/api/admin/catalog")
@RequiredArgsConstructor
public class CatalogMaterialsAdminController {

    private final NewCatalogAdminService service;

    // ----------------------------------------------------------- tipos-material

    @GetMapping("/tipos-material")
    public List<TipoMaterialEntity> listTiposMaterial() {
        return service.listTiposMaterial();
    }

    @PostMapping("/tipos-material")
    public TipoMaterialEntity createTipoMaterial(@RequestBody TipoMaterialEntity body) {
        return service.createTipoMaterial(body);
    }

    @PutMapping("/tipos-material/{id}")
    public TipoMaterialEntity updateTipoMaterial(@PathVariable Long id, @RequestBody TipoMaterialEntity body) {
        return service.updateTipoMaterial(id, body);
    }

    @DeleteMapping("/tipos-material/{id}")
    public void deleteTipoMaterial(@PathVariable Long id) {
        service.deleteTipoMaterial(id);
    }

    // --------------------------------------------------------------- materiales

    @GetMapping("/materiales")
    public List<MaterialEntity> listMateriales(@RequestParam(required = false) Long tipoMaterialId) {
        return service.listMateriales(tipoMaterialId);
    }

    @GetMapping("/materiales/{id}")
    public MaterialEntity getMaterial(@PathVariable Long id) {
        return service.getMaterial(id);
    }

    @PostMapping("/materiales")
    public MaterialEntity createMaterial(@RequestBody MaterialEntity body) {
        return service.createMaterial(body);
    }

    @PutMapping("/materiales/{id}")
    public MaterialEntity updateMaterial(@PathVariable Long id, @RequestBody MaterialEntity body) {
        return service.updateMaterial(id, body);
    }

    @DeleteMapping("/materiales/{id}")
    public void deleteMaterial(@PathVariable Long id) {
        service.deleteMaterial(id);
    }

    // -------------------------------------------------------- material-imagenes

    @GetMapping("/material-imagenes")
    public List<MaterialImagenEntity> listMaterialImagenes(@RequestParam(required = false) Long materialId) {
        return service.listMaterialImagenes(materialId);
    }

    @PostMapping("/material-imagenes")
    public MaterialImagenEntity createMaterialImagen(@RequestBody MaterialImagenEntity body) {
        return service.createMaterialImagen(body);
    }

    @PutMapping("/material-imagenes/{id}")
    public MaterialImagenEntity updateMaterialImagen(@PathVariable Long id,
                                                     @RequestBody MaterialImagenEntity body) {
        return service.updateMaterialImagen(id, body);
    }

    @DeleteMapping("/material-imagenes/{id}")
    public void deleteMaterialImagen(@PathVariable Long id) {
        service.deleteMaterialImagen(id);
    }

    // ------------------------------------------------------------------ colores

    @GetMapping("/colores")
    public List<ColorEntity> listColores() {
        return service.listColores();
    }

    @GetMapping("/colores/{id}")
    public ColorEntity getColor(@PathVariable Long id) {
        return service.getColor(id);
    }

    @PostMapping("/colores")
    public ColorEntity createColor(@RequestBody ColorEntity body) {
        return service.createColor(body);
    }

    @PutMapping("/colores/{id}")
    public ColorEntity updateColor(@PathVariable Long id, @RequestBody ColorEntity body) {
        return service.updateColor(id, body);
    }

    @DeleteMapping("/colores/{id}")
    public void deleteColor(@PathVariable Long id) {
        service.deleteColor(id);
    }

    // --------------------------------------------------------- material-colores

    @GetMapping("/material-colores")
    public List<MaterialColorEntity> listMaterialColores(@RequestParam(required = false) Long materialId) {
        return service.listMaterialColores(materialId);
    }

    @PostMapping("/material-colores")
    public MaterialColorEntity createMaterialColor(@RequestBody MaterialColorEntity body) {
        return service.createMaterialColor(body);
    }

    @DeleteMapping("/material-colores/{materialId}/{colorId}")
    public void deleteMaterialColor(@PathVariable Long materialId, @PathVariable Long colorId) {
        service.deleteMaterialColor(materialId, colorId);
    }

    // ----------------------------------------------------------------- gramajes

    @GetMapping("/gramajes")
    public List<GramajeEntity> listGramajes(@RequestParam(required = false) Long materialId) {
        return service.listGramajes(materialId);
    }

    @GetMapping("/gramajes/{id}")
    public GramajeEntity getGramaje(@PathVariable Long id) {
        return service.getGramaje(id);
    }

    @PostMapping("/gramajes")
    public GramajeEntity createGramaje(@RequestBody GramajeEntity body) {
        return service.createGramaje(body);
    }

    @PutMapping("/gramajes/{id}")
    public GramajeEntity updateGramaje(@PathVariable Long id, @RequestBody GramajeEntity body) {
        return service.updateGramaje(id, body);
    }

    @DeleteMapping("/gramajes/{id}")
    public void deleteGramaje(@PathVariable Long id) {
        service.deleteGramaje(id);
    }

    // ------------------------------------------------------------- papeles-forro

    @GetMapping("/papeles-forro")
    public List<PapelForroEntity> listPapelesForro() {
        return service.listPapelesForro();
    }

    @GetMapping("/papeles-forro/{id}")
    public PapelForroEntity getPapelForro(@PathVariable Long id) {
        return service.getPapelForro(id);
    }

    @PostMapping("/papeles-forro")
    public PapelForroEntity createPapelForro(@RequestBody PapelForroEntity body) {
        return service.createPapelForro(body);
    }

    @PutMapping("/papeles-forro/{id}")
    public PapelForroEntity updatePapelForro(@PathVariable Long id, @RequestBody PapelForroEntity body) {
        return service.updatePapelForro(id, body);
    }

    @DeleteMapping("/papeles-forro/{id}")
    public void deletePapelForro(@PathVariable Long id) {
        service.deletePapelForro(id);
    }

    // ------------------------------------------------------------------ viniles

    @GetMapping("/viniles")
    public List<VinilEntity> listViniles() {
        return service.listViniles();
    }

    @GetMapping("/viniles/{id}")
    public VinilEntity getVinil(@PathVariable Long id) {
        return service.getVinil(id);
    }

    @PostMapping("/viniles")
    public VinilEntity createVinil(@RequestBody VinilEntity body) {
        return service.createVinil(body);
    }

    @PutMapping("/viniles/{id}")
    public VinilEntity updateVinil(@PathVariable Long id, @RequestBody VinilEntity body) {
        return service.updateVinil(id, body);
    }

    @DeleteMapping("/viniles/{id}")
    public void deleteVinil(@PathVariable Long id) {
        service.deleteVinil(id);
    }
}
