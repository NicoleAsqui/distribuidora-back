package ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "quote_engine_configs")
public class QuoteEngineConfigEntity {
    @Id
    private String motor;

    @Column(name = "config_json", nullable = false, columnDefinition = "TEXT")
    private String configJson;

    public String getMotor() { return motor; }
    public void setMotor(String motor) { this.motor = motor; }
    public String getConfigJson() { return configJson; }
    public void setConfigJson(String configJson) { this.configJson = configJson; }
}
