package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "materials")
public class MaterialEntity {
    @Id
    private String id;
    @Column(nullable = false)
    private String name;
    @Convert(converter = StringListConverter.class)
    @Column(length = 2000)
    private List<String> options = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
}
