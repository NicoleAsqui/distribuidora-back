package ec.distribuidoraguayaquil.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SriFacturacionConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.sri")
    public SriFacturacionProperties sriFacturacionProperties() {
        return new SriFacturacionProperties();
    }

    public static class SriFacturacionProperties {
        private String apiUrl = "";
        private String apiToken = "";
        private String ambiente = "1";
        private final Emisor emisor = new Emisor();

        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        public String getApiToken() { return apiToken; }
        public void setApiToken(String apiToken) { this.apiToken = apiToken; }
        public String getAmbiente() { return ambiente; }
        public void setAmbiente(String ambiente) { this.ambiente = ambiente; }
        public Emisor getEmisor() { return emisor; }

        public boolean isConfigured() {
            return apiUrl != null && !apiUrl.isBlank()
                    && apiToken != null && !apiToken.isBlank()
                    && emisor.getRuc() != null && !emisor.getRuc().isBlank()
                    && emisor.getRazonSocial() != null && !emisor.getRazonSocial().isBlank()
                    && emisor.getDirMatriz() != null && !emisor.getDirMatriz().isBlank();
        }

        public static class Emisor {
            private String ruc = "";
            private String razonSocial = "";
            private String nombreComercial = "";
            private String dirMatriz = "";
            private String dirEstablecimiento = "";
            private String establecimiento = "001";
            private String puntoEmision = "001";
            private String obligadoContabilidad = "SI";

            public String getRuc() { return ruc; }
            public void setRuc(String ruc) { this.ruc = ruc; }
            public String getRazonSocial() { return razonSocial; }
            public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
            public String getNombreComercial() { return nombreComercial; }
            public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }
            public String getDirMatriz() { return dirMatriz; }
            public void setDirMatriz(String dirMatriz) { this.dirMatriz = dirMatriz; }
            public String getDirEstablecimiento() { return dirEstablecimiento; }
            public void setDirEstablecimiento(String dirEstablecimiento) { this.dirEstablecimiento = dirEstablecimiento; }
            public String getEstablecimiento() { return establecimiento; }
            public void setEstablecimiento(String establecimiento) { this.establecimiento = establecimiento; }
            public String getPuntoEmision() { return puntoEmision; }
            public void setPuntoEmision(String puntoEmision) { this.puntoEmision = puntoEmision; }
            public String getObligadoContabilidad() { return obligadoContabilidad; }
            public void setObligadoContabilidad(String obligadoContabilidad) { this.obligadoContabilidad = obligadoContabilidad; }
        }
    }
}
