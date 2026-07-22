package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "box_models")
public class BoxModelEntity {
    @Id
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(name = "category_id", nullable = false)
    private String categoryId;
    @Column(length = 2000)
    private String description;
    @Convert(converter = StringListConverter.class)
    @Column(length = 4000)
    private List<String> photos = new ArrayList<>();
    @Convert(converter = StringListConverter.class)
    @Column(length = 2000)
    private List<String> materials = new ArrayList<>();
    @Convert(converter = StringListConverter.class)
    @Column(length = 2000)
    private List<String> finishes = new ArrayList<>();
    @Convert(converter = StringListConverter.class)
    @Column(length = 2000)
    private List<String> colors = new ArrayList<>();
    @Column(name = "min_qty", nullable = false)
    private int minQty;
    @Column(name = "lead_days", nullable = false)
    private int leadDays;
    @Convert(converter = StringListConverter.class)
    @Column(length = 2000)
    private List<String> tags = new ArrayList<>();
    @Column(nullable = false)
    private boolean active = true;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getPhotos() { return photos; }
    public void setPhotos(List<String> photos) { this.photos = photos; }
    public List<String> getMaterials() { return materials; }
    public void setMaterials(List<String> materials) { this.materials = materials; }
    public List<String> getFinishes() { return finishes; }
    public void setFinishes(List<String> finishes) { this.finishes = finishes; }
    public List<String> getColors() { return colors; }
    public void setColors(List<String> colors) { this.colors = colors; }
    public int getMinQty() { return minQty; }
    public void setMinQty(int minQty) { this.minQty = minQty; }
    public int getLeadDays() { return leadDays; }
    public void setLeadDays(int leadDays) { this.leadDays = leadDays; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
