package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.NewCatalogAdminService;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.IdeaVarianteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.MedidaEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.PrecioEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.TagEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteImagenEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteTagEntity;
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

/**
 * Administración del catálogo nuevo: diseños, medidas, variantes, precios, ideas y tags.
 * Materiales en {@link CatalogMaterialsAdminController}, construcción en
 * {@link CatalogBuildAdminController}. Autenticación vía interceptor de /api/admin/**.
 */
@RestController
@RequestMapping("/api/admin/catalog")
@RequiredArgsConstructor
public class NewCatalogAdminController {

    private final NewCatalogAdminService service;

    // ------------------------------------------------------------------ diseños

    @GetMapping("/disenos")
    public List<DisenoEntity> listDisenos() {
        return service.listDisenos();
    }

    @GetMapping("/disenos/{id}")
    public DisenoEntity getDiseno(@PathVariable Long id) {
        return service.getDiseno(id);
    }

    @PostMapping("/disenos")
    public DisenoEntity createDiseno(@RequestBody DisenoEntity body) {
        return service.createDiseno(body);
    }

    @PutMapping("/disenos/{id}")
    public DisenoEntity updateDiseno(@PathVariable Long id, @RequestBody DisenoEntity body) {
        return service.updateDiseno(id, body);
    }

    @DeleteMapping("/disenos/{id}")
    public void deleteDiseno(@PathVariable Long id) {
        service.deleteDiseno(id);
    }

    // ------------------------------------------------------------------ medidas

    @GetMapping("/medidas")
    public List<MedidaEntity> listMedidas() {
        return service.listMedidas();
    }

    @GetMapping("/medidas/{id}")
    public MedidaEntity getMedida(@PathVariable Long id) {
        return service.getMedida(id);
    }

    @PostMapping("/medidas")
    public MedidaEntity createMedida(@RequestBody MedidaEntity body) {
        return service.createMedida(body);
    }

    @PutMapping("/medidas/{id}")
    public MedidaEntity updateMedida(@PathVariable Long id, @RequestBody MedidaEntity body) {
        return service.updateMedida(id, body);
    }

    @DeleteMapping("/medidas/{id}")
    public void deleteMedida(@PathVariable Long id) {
        service.deleteMedida(id);
    }

    // ---------------------------------------------------------------- variantes

    @GetMapping("/variantes")
    public List<VarianteEntity> listVariantes(@RequestParam(required = false) Long disenoId) {
        return service.listVariantes(disenoId);
    }

    @GetMapping("/variantes/{id}")
    public VarianteEntity getVariante(@PathVariable Long id) {
        return service.getVariante(id);
    }

    @PostMapping("/variantes")
    public VarianteEntity createVariante(@RequestBody VarianteEntity body) {
        return service.createVariante(body);
    }

    @PutMapping("/variantes/{id}")
    public VarianteEntity updateVariante(@PathVariable Long id, @RequestBody VarianteEntity body) {
        return service.updateVariante(id, body);
    }

    @DeleteMapping("/variantes/{id}")
    public void deleteVariante(@PathVariable Long id) {
        service.deleteVariante(id);
    }

    // -------------------------------------------------------- variante-imagenes

    @GetMapping("/variante-imagenes")
    public List<VarianteImagenEntity> listVarianteImagenes(@RequestParam(required = false) Long varianteId) {
        return service.listVarianteImagenes(varianteId);
    }

    @PostMapping("/variante-imagenes")
    public VarianteImagenEntity createVarianteImagen(@RequestBody VarianteImagenEntity body) {
        return service.createVarianteImagen(body);
    }

    @PutMapping("/variante-imagenes/{id}")
    public VarianteImagenEntity updateVarianteImagen(@PathVariable Long id,
                                                     @RequestBody VarianteImagenEntity body) {
        return service.updateVarianteImagen(id, body);
    }

    @DeleteMapping("/variante-imagenes/{id}")
    public void deleteVarianteImagen(@PathVariable Long id) {
        service.deleteVarianteImagen(id);
    }

    // ------------------------------------------------------------------ precios

    @GetMapping("/precios")
    public List<PrecioEntity> listPrecios(@RequestParam(required = false) Long varianteId) {
        return service.listPrecios(varianteId);
    }

    @GetMapping("/precios/{id}")
    public PrecioEntity getPrecio(@PathVariable Long id) {
        return service.getPrecio(id);
    }

    @PostMapping("/precios")
    public PrecioEntity createPrecio(@RequestBody PrecioEntity body) {
        return service.createPrecio(body);
    }

    @PutMapping("/precios/{id}")
    public PrecioEntity updatePrecio(@PathVariable Long id, @RequestBody PrecioEntity body) {
        return service.updatePrecio(id, body);
    }

    @DeleteMapping("/precios/{id}")
    public void deletePrecio(@PathVariable Long id) {
        service.deletePrecio(id);
    }

    // -------------------------------------------------------------------- ideas

    @GetMapping("/ideas")
    public List<IdeaEntity> listIdeas() {
        return service.listIdeas();
    }

    @GetMapping("/ideas/{id}")
    public IdeaEntity getIdea(@PathVariable Long id) {
        return service.getIdea(id);
    }

    @PostMapping("/ideas")
    public IdeaEntity createIdea(@RequestBody IdeaEntity body) {
        return service.createIdea(body);
    }

    @PutMapping("/ideas/{id}")
    public IdeaEntity updateIdea(@PathVariable Long id, @RequestBody IdeaEntity body) {
        return service.updateIdea(id, body);
    }

    @DeleteMapping("/ideas/{id}")
    public void deleteIdea(@PathVariable Long id) {
        service.deleteIdea(id);
    }

    // ------------------------------------------------------------ idea-imagenes

    @GetMapping("/idea-imagenes")
    public List<IdeaImagenEntity> listIdeaImagenes(@RequestParam(required = false) Long ideaId) {
        return service.listIdeaImagenes(ideaId);
    }

    @PostMapping("/idea-imagenes")
    public IdeaImagenEntity createIdeaImagen(@RequestBody IdeaImagenEntity body) {
        return service.createIdeaImagen(body);
    }

    @PutMapping("/idea-imagenes/{id}")
    public IdeaImagenEntity updateIdeaImagen(@PathVariable Long id, @RequestBody IdeaImagenEntity body) {
        return service.updateIdeaImagen(id, body);
    }

    @DeleteMapping("/idea-imagenes/{id}")
    public void deleteIdeaImagen(@PathVariable Long id) {
        service.deleteIdeaImagen(id);
    }

    // ----------------------------------------------------------- idea-variantes

    @GetMapping("/idea-variantes")
    public List<IdeaVarianteEntity> listIdeaVariantes(@RequestParam(required = false) Long ideaId) {
        return service.listIdeaVariantes(ideaId);
    }

    @GetMapping("/idea-variantes/{id}")
    public IdeaVarianteEntity getIdeaVariante(@PathVariable Long id) {
        return service.getIdeaVariante(id);
    }

    @PostMapping("/idea-variantes")
    public IdeaVarianteEntity createIdeaVariante(@RequestBody IdeaVarianteEntity body) {
        return service.createIdeaVariante(body);
    }

    @PutMapping("/idea-variantes/{id}")
    public IdeaVarianteEntity updateIdeaVariante(@PathVariable Long id, @RequestBody IdeaVarianteEntity body) {
        return service.updateIdeaVariante(id, body);
    }

    @DeleteMapping("/idea-variantes/{id}")
    public void deleteIdeaVariante(@PathVariable Long id) {
        service.deleteIdeaVariante(id);
    }

    // --------------------------------------------------------------------- tags

    @GetMapping("/tags")
    public List<TagEntity> listTags() {
        return service.listTags();
    }

    @PostMapping("/tags")
    public TagEntity createTag(@RequestBody TagEntity body) {
        return service.createTag(body);
    }

    @PutMapping("/tags/{id}")
    public TagEntity updateTag(@PathVariable Long id, @RequestBody TagEntity body) {
        return service.updateTag(id, body);
    }

    @DeleteMapping("/tags/{id}")
    public void deleteTag(@PathVariable Long id) {
        service.deleteTag(id);
    }

    // ------------------------------------------------------------ variante-tags

    @GetMapping("/variante-tags")
    public List<VarianteTagEntity> listVarianteTags(@RequestParam(required = false) Long varianteId) {
        return service.listVarianteTags(varianteId);
    }

    @PostMapping("/variante-tags")
    public VarianteTagEntity createVarianteTag(@RequestBody VarianteTagEntity body) {
        return service.createVarianteTag(body);
    }

    @DeleteMapping("/variante-tags/{varianteId}/{tagId}")
    public void deleteVarianteTag(@PathVariable Long varianteId, @PathVariable Long tagId) {
        service.deleteVarianteTag(varianteId, tagId);
    }
}
