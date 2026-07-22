package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.model.BoxModel;
import ec.distribuidoraguayaquil.domain.model.Category;
import ec.distribuidoraguayaquil.domain.model.Color;
import ec.distribuidoraguayaquil.domain.model.Finish;
import ec.distribuidoraguayaquil.domain.model.Material;
import ec.distribuidoraguayaquil.domain.model.Tag;
import ec.distribuidoraguayaquil.domain.port.in.CatalogAdminUseCase;
import ec.distribuidoraguayaquil.domain.port.out.BoxModelRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.CategoryRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.ColorRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.FinishRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.MaterialRepositoryPort;
import ec.distribuidoraguayaquil.domain.port.out.TagRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CatalogAdminService implements CatalogAdminUseCase {

    private final CategoryRepositoryPort categoryRepository;
    private final MaterialRepositoryPort materialRepository;
    private final ColorRepositoryPort colorRepository;
    private final FinishRepositoryPort finishRepository;
    private final TagRepositoryPort tagRepository;
    private final BoxModelRepositoryPort boxModelRepository;

    public CatalogAdminService(
            CategoryRepositoryPort categoryRepository,
            MaterialRepositoryPort materialRepository,
            ColorRepositoryPort colorRepository,
            FinishRepositoryPort finishRepository,
            TagRepositoryPort tagRepository,
            BoxModelRepositoryPort boxModelRepository) {
        this.categoryRepository = categoryRepository;
        this.materialRepository = materialRepository;
        this.colorRepository = colorRepository;
        this.finishRepository = finishRepository;
        this.tagRepository = tagRepository;
        this.boxModelRepository = boxModelRepository;
    }

    @Override
    public List<Category> listCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category saveCategory(Category category) {
        String id = blank(category.id()) ? UUID.randomUUID().toString() : category.id();
        return categoryRepository.save(new Category(id, category.name(), category.slug()));
    }

    @Override
    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<Material> listMaterials() {
        return materialRepository.findAll();
    }

    @Override
    public Material saveMaterial(Material material) {
        String id = blank(material.id()) ? UUID.randomUUID().toString() : material.id();
        return materialRepository.save(new Material(id, material.name(), material.options()));
    }

    @Override
    public void deleteMaterial(String id) {
        materialRepository.deleteById(id);
    }

    @Override
    public List<Color> listColors() {
        return colorRepository.findAll();
    }

    @Override
    public Color saveColor(Color color) {
        String id = blank(color.id()) ? UUID.randomUUID().toString() : color.id();
        return colorRepository.save(new Color(id, color.name(), color.hex(), color.available()));
    }

    @Override
    public void deleteColor(String id) {
        colorRepository.deleteById(id);
    }

    @Override
    public List<Finish> listFinishes() {
        return finishRepository.findAll();
    }

    @Override
    public Finish saveFinish(Finish finish) {
        String id = blank(finish.id()) ? UUID.randomUUID().toString() : finish.id();
        return finishRepository.save(new Finish(id, finish.name(), finish.description()));
    }

    @Override
    public void deleteFinish(String id) {
        finishRepository.deleteById(id);
    }

    @Override
    public List<Tag> listTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag saveTag(Tag tag) {
        String id = blank(tag.id()) ? UUID.randomUUID().toString() : tag.id();
        return tagRepository.save(new Tag(id, tag.name(), tag.color()));
    }

    @Override
    public void deleteTag(String id) {
        tagRepository.deleteById(id);
    }

    @Override
    public List<BoxModel> listModels() {
        return boxModelRepository.findAll();
    }

    @Override
    public BoxModel saveModel(BoxModel model) {
        String id = blank(model.id()) ? UUID.randomUUID().toString() : model.id();
        return boxModelRepository.save(new BoxModel(
                id, model.name(), model.categoryId(), model.description(), model.photos(),
                model.materials(), model.finishes(), model.colors(), model.minQty(),
                model.leadDays(), model.tags(), model.active()));
    }

    @Override
    public void deleteModel(String id) {
        boxModelRepository.deleteById(id);
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
