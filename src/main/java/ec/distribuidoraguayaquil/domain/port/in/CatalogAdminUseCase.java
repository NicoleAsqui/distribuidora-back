package ec.distribuidoraguayaquil.domain.port.in;

import ec.distribuidoraguayaquil.domain.model.BoxModel;
import ec.distribuidoraguayaquil.domain.model.Category;
import ec.distribuidoraguayaquil.domain.model.Color;
import ec.distribuidoraguayaquil.domain.model.Finish;
import ec.distribuidoraguayaquil.domain.model.Material;
import ec.distribuidoraguayaquil.domain.model.Tag;

import java.util.List;

public interface CatalogAdminUseCase {
    List<Category> listCategories();
    Category saveCategory(Category category);
    void deleteCategory(String id);

    List<Material> listMaterials();
    Material saveMaterial(Material material);
    void deleteMaterial(String id);

    List<Color> listColors();
    Color saveColor(Color color);
    void deleteColor(String id);

    List<Finish> listFinishes();
    Finish saveFinish(Finish finish);
    void deleteFinish(String id);

    List<Tag> listTags();
    Tag saveTag(Tag tag);
    void deleteTag(String id);

    List<BoxModel> listModels();
    BoxModel saveModel(BoxModel model);
    void deleteModel(String id);
}
