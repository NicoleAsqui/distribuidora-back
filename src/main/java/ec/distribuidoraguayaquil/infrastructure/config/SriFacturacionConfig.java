package ec.distribuidoraguayaquil.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SriFacturacionConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.sri")
    public SriFacturacionProperties sriFacturacionProperties() {
        return new SriFacturacionProperties();
    }

    public static class SriFacturacionProperties {
        /** 1 = pruebas, 2 = producción. */
        private String ambiente = "1";
        /** Primer secuencial a usar si aún no hay facturas en este ambiente. */
        private int secuencialInicial = 1;
        private final Emisor emisor = new Emisor();

        public String getAmbiente() { return ambiente == null || ambiente.isBlank() ? "1" : ambiente.trim(); }
        public void setAmbiente(String ambiente) { this.ambiente = ambiente; }
        public int getSecuencialInicial() { return Math.max(1, secuencialInicial); }
        public void setSecuencialInicial(int secuencialInicial) { this.secuencialInicial = secuencialInicial; }
        public Emisor getEmisor() { return emisor; }

        /** Qué falta para poder emitir (vacío = listo). */
        public List<String> missing() {
            List<String> out = new ArrayList<>();
            if (blank(emisor.ruc)) out.add("SRI_EMISOR_RUC");
            if (blank(emisor.razonSocial)) out.add("SRI_EMISOR_RAZON_SOCIAL");
            if (blank(emisor.dirMatriz)) out.add("SRI_EMISOR_DIR_MATRIZ");
            return out;
        }

        public boolean isConfigured() {
            return missing().isEmpty();
        }

        private static boolean blank(String s) {
            return s == null || s.isBlank();
        }

        public static class Emisor {
            private String ruc = "";
            private String razonSocial = "";
            private String nombreComercial = "";
            private String dirMatriz = "";
            private String dirEstablecimiento = "";
            private String establecimiento = "001";
            private String puntoEmision = "001";
            private String obligadoContabilidad = "NO";
            /** Leyenda RIMPE si aplica, p. ej. "CONTRIBUYENTE RÉGIMEN RIMPE". */
            private String contribuyenteRimpe = "";
            /** Logo del establecimiento para el PDF (RIDE). */
            private String logoUrl = "";

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
            public String getContribuyenteRimpe() { return contribuyenteRimpe; }
            public void setContribuyenteRimpe(String contribuyenteRimpe) { this.contribuyenteRimpe = contribuyenteRimpe; }
            public String getLogoUrl() { return logoUrl; }
            public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
        }
    }
}
