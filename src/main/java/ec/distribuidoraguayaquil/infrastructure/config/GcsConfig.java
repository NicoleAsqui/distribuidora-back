package ec.distribuidoraguayaquil.infrastructure.config;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GcsConfig {

    @Bean
    @ConfigurationProperties(prefix = "app.gcs")
    public GcsProperties gcsProperties() {
        return new GcsProperties();
    }

    @Bean
    public Storage googleCloudStorage() {
        return StorageOptions.getDefaultInstance().getService();
    }

    public static class GcsProperties {
        private String bucketName = "";
        private String uploadPrefix = "products";
        private String publicBaseUrl = "";
        private String cacheControl = "public, max-age=31536000, immutable";

        public String getBucketName() { return bucketName; }
        public void setBucketName(String bucketName) { this.bucketName = bucketName; }
        public String getUploadPrefix() { return uploadPrefix; }
        public void setUploadPrefix(String uploadPrefix) { this.uploadPrefix = uploadPrefix; }
        public String getPublicBaseUrl() { return publicBaseUrl; }
        public void setPublicBaseUrl(String publicBaseUrl) { this.publicBaseUrl = publicBaseUrl; }
        public String getCacheControl() { return cacheControl; }
        public void setCacheControl(String cacheControl) { this.cacheControl = cacheControl; }

        public boolean isConfigured() {
            return bucketName != null && !bucketName.isBlank();
        }

        public String publicBase() {
            if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
                return publicBaseUrl.replaceAll("/$", "");
            }
            return "https://storage.googleapis.com/" + bucketName;
        }
    }
}
