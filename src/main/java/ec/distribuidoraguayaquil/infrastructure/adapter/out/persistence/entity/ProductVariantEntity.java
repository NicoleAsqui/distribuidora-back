package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
public class ProductVariantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    private String tag;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProductEntity getProduct() { return product; }
    public void setProduct(ProductEntity product) { this.product = product; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
}
