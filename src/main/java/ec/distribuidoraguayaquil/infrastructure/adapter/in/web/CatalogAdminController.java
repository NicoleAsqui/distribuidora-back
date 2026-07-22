package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.domain.model.BoxModel;
import ec.distribuidoraguayaquil.domain.model.Category;
import ec.distribuidoraguayaquil.domain.model.Color;
import ec.distribuidoraguayaquil.domain.model.Finish;
import ec.distribuidoraguayaquil.domain.model.Material;
import ec.distribuidoraguayaquil.domain.model.Tag;
import ec.distribuidoraguayaquil.domain.port.in.CatalogAdminUseCase;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/catalog")
public class CatalogAdminController {

    private final CatalogAdminUseCase catalogAdminUseCase;

    public CatalogAdminController(CatalogAdminUseCase catalogAdminUseCase) {
        this.catalogAdminUseCase = catalogAdminUseCase;
    }

    @GetMapping("/categories")
    public List<Category> categories() { return catalogAdminUseCase.listCategories(); }

    @PostMapping("/categories")
    public Category createCategory(@RequestBody Category category) { return catalogAdminUseCase.saveCategory(category); }

    @PutMapping("/categories/{id}")
    public Category updateCategory(@PathVariable String id, @RequestBody Category category) {
        return catalogAdminUseCase.saveCategory(new Category(id, category.name(), category.slug()));
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable String id) { catalogAdminUseCase.deleteCategory(id); }

    @GetMapping("/materials")
    public List<Material> materials() { return catalogAdminUseCase.listMaterials(); }

    @PostMapping("/materials")
    public Material createMaterial(@RequestBody Material material) { return catalogAdminUseCase.saveMaterial(material); }

    @PutMapping("/materials/{id}")
    public Material updateMaterial(@PathVariable String id, @RequestBody Material material) {
        return catalogAdminUseCase.saveMaterial(new Material(id, material.name(), material.options()));
    }

    @DeleteMapping("/materials/{id}")
    public void deleteMaterial(@PathVariable String id) { catalogAdminUseCase.deleteMaterial(id); }

    @GetMapping("/colors")
    public List<Color> colors() { return catalogAdminUseCase.listColors(); }

    @PostMapping("/colors")
    public Color createColor(@RequestBody Color color) { return catalogAdminUseCase.saveColor(color); }

    @PutMapping("/colors/{id}")
    public Color updateColor(@PathVariable String id, @RequestBody Color color) {
        return catalogAdminUseCase.saveColor(new Color(id, color.name(), color.hex(), color.available()));
    }

    @DeleteMapping("/colors/{id}")
    public void deleteColor(@PathVariable String id) { catalogAdminUseCase.deleteColor(id); }

    @GetMapping("/finishes")
    public List<Finish> finishes() { return catalogAdminUseCase.listFinishes(); }

    @PostMapping("/finishes")
    public Finish createFinish(@RequestBody Finish finish) { return catalogAdminUseCase.saveFinish(finish); }

    @PutMapping("/finishes/{id}")
    public Finish updateFinish(@PathVariable String id, @RequestBody Finish finish) {
        return catalogAdminUseCase.saveFinish(new Finish(id, finish.name(), finish.description()));
    }

    @DeleteMapping("/finishes/{id}")
    public void deleteFinish(@PathVariable String id) { catalogAdminUseCase.deleteFinish(id); }

    @GetMapping("/tags")
    public List<Tag> tags() { return catalogAdminUseCase.listTags(); }

    @PostMapping("/tags")
    public Tag createTag(@RequestBody Tag tag) { return catalogAdminUseCase.saveTag(tag); }

    @PutMapping("/tags/{id}")
    public Tag updateTag(@PathVariable String id, @RequestBody Tag tag) {
        return catalogAdminUseCase.saveTag(new Tag(id, tag.name(), tag.color()));
    }

    @DeleteMapping("/tags/{id}")
    public void deleteTag(@PathVariable String id) { catalogAdminUseCase.deleteTag(id); }

    @GetMapping("/models")
    public List<BoxModel> models() { return catalogAdminUseCase.listModels(); }

    @PostMapping("/models")
    public BoxModel createModel(@RequestBody BoxModel model) { return catalogAdminUseCase.saveModel(model); }

    @PutMapping("/models/{id}")
    public BoxModel updateModel(@PathVariable String id, @RequestBody BoxModel model) {
        return catalogAdminUseCase.saveModel(new BoxModel(
                id, model.name(), model.categoryId(), model.description(), model.photos(),
                model.materials(), model.finishes(), model.colors(), model.minQty(),
                model.leadDays(), model.tags(), model.active()));
    }

    @DeleteMapping("/models/{id}")
    public void deleteModel(@PathVariable String id) { catalogAdminUseCase.deleteModel(id); }
}
