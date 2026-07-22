package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "colors")
public class ColorEntity {
    @Id
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String hex;
    @Column(nullable = false)
    private boolean available = true;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHex() { return hex; }
    public void setHex(String hex) { this.hex = hex; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
