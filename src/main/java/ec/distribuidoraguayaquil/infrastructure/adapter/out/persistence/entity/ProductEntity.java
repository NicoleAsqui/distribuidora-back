package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String ref;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(name = "short_desc", length = 1000)
    private String shortDesc;

    @Column(nullable = false)
    private boolean topProduct;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ProductVariantEntity> variants = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRef() { return ref; }
    public void setRef(String ref) { this.ref = ref; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getShortDesc() { return shortDesc; }
    public void setShortDesc(String shortDesc) { this.shortDesc = shortDesc; }
    public boolean isTopProduct() { return topProduct; }
    public void setTopProduct(boolean topProduct) { this.topProduct = topProduct; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<ProductVariantEntity> getVariants() { return variants; }
    public void setVariants(List<ProductVariantEntity> variants) { this.variants = variants; }
}
