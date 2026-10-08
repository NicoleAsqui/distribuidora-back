package ec.distribuidoraguayaquil.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Aplica migraciones idempotentes del catálogo al arrancar (p. ej. diseno_imagenes en Neon).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CatalogSchemaMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CatalogSchemaMigrationRunner.class);

    private final DataSource dataSource;

    public CatalogSchemaMigrationRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        runScript("007_diseno_imagenes.sql");
        runScript("008_texturas_cartulina.sql");
        runScript("009_pricing_quote_edit_token.sql");
        runScript("010_diseno_motor.sql");
        runScript("011_diseno_texturas.sql");
        runScript("012_diseno_video_url.sql");
        runScript("013_guias.sql");
        runScript("014_guias_video.sql");
        runScript("015_diseno_textura_color.sql");
        runScript("016_medida_alto_opcional.sql");
        runScript("017_pricing_quote_manual.sql");
        runScript("018_diseno_nombre_por_seccion.sql");
        runScript("019_variante_unidad_venta.sql");
        runScript("020_variante_destacado.sql");
        runScript("021_idea_destacado.sql");
        runScript("022_textura_tipo.sql");
    }

    private void runScript(String name) {
        var resource = new ClassPathResource("db/migrations/" + name);
        if (!resource.exists()) {
            return;
        }
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, resource);
            log.info("Migración catálogo aplicada: {}", name);
        } catch (Exception e) {
            log.error("No se pudo aplicar {} — revisa la base de datos", name, e);
        }
    }
}
