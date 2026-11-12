package ec.distribuidoraguayaquil.application.service.sri;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.SriAjustesEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.SriAjustesJpaRepository;
import ec.distribuidoraguayaquil.infrastructure.config.SriFacturacionConfig.SriFacturacionProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

/** Ajustes del emisor y ambiente SRI guardados desde el admin; sin ajustes guardados se usan las variables de entorno. */
@Service
public class SriAjustesStore {

    private static final int ID = 1;

    public record Ajustes(String ambiente, Integer secuencialInicial, String ruc, String razonSocial,
                          String nombreComercial, String dirMatriz, String dirEstablecimiento, String establecimiento,
                          String puntoEmision, String obligadoContabilidad, String contribuyenteRimpe) {}

    private final SriAjustesJpaRepository repository;
    private final SriFacturacionProperties env;
    private final ObjectMapper objectMapper;

    public SriAjustesStore(SriAjustesJpaRepository repository, SriFacturacionProperties env, ObjectMapper objectMapper) {
        this.repository = repository;
        this.env = env;
        this.objectMapper = objectMapper;
    }

    /** Configuración vigente: lo guardado en el admin, o las variables de entorno. */
    public SriFacturacionProperties vigente() {
        var row = repository.findById(ID);
        if (row.isEmpty()) return env;
        Ajustes a = objectMapper.readValue(row.get().getDatosJson(), Ajustes.class);
        SriFacturacionProperties p = new SriFacturacionProperties();
        p.setAmbiente(a.ambiente());
        p.setSecuencialInicial(a.secuencialInicial() == null ? 1 : a.secuencialInicial());
        var e = p.getEmisor();
        e.setRuc(a.ruc());
        e.setRazonSocial(a.razonSocial());
        e.setNombreComercial(a.nombreComercial());
        e.setDirMatriz(a.dirMatriz());
        e.setDirEstablecimiento(a.dirEstablecimiento());
        e.setEstablecimiento(a.establecimiento());
        e.setPuntoEmision(a.puntoEmision());
        e.setObligadoContabilidad(a.obligadoContabilidad());
        e.setContribuyenteRimpe(a.contribuyenteRimpe());
        e.setLogoUrl(env.getEmisor().getLogoUrl());
        return p;
    }

    public SriFacturacionProperties guardar(Ajustes a, boolean confirmarProduccion, String actor) {
        String ambiente = "2".equals(a.ambiente()) ? "2" : "1";
        if ("2".equals(ambiente) && !"2".equals(vigente().getAmbiente()) && !confirmarProduccion) {
            bad("Confirma el paso a producción: desde ahí las facturas son reales y tienen validez tributaria.");
        }
        String ruc = t(a.ruc());
        if (!ruc.matches("\\d{10}001")) bad("El RUC debe tener 13 dígitos y terminar en 001.");
        if (t(a.razonSocial()).isEmpty()) bad("Escribe la razón social (tal como sale en el RUC).");
        if (t(a.dirMatriz()).isEmpty()) bad("Escribe la dirección matriz.");
        if (!t(a.establecimiento()).matches("\\d{3}") || !t(a.puntoEmision()).matches("\\d{3}")) {
            bad("Establecimiento y punto de emisión van con 3 dígitos, por ejemplo 002 y 002.");
        }
        int sec = a.secuencialInicial() == null ? 1 : a.secuencialInicial();
        if (sec < 1 || sec > 999_999_999) bad("El secuencial inicial debe estar entre 1 y 999999999.");
        Ajustes limpio = new Ajustes(ambiente, sec, ruc, t(a.razonSocial()).toUpperCase(), t(a.nombreComercial()),
                t(a.dirMatriz()), t(a.dirEstablecimiento()), t(a.establecimiento()), t(a.puntoEmision()),
                "SI".equalsIgnoreCase(t(a.obligadoContabilidad())) ? "SI" : "NO", t(a.contribuyenteRimpe()));
        SriAjustesEntity row = repository.findById(ID).orElseGet(SriAjustesEntity::new);
        row.setId(ID);
        row.setDatosJson(objectMapper.writeValueAsString(limpio));
        row.setUpdatedAt(Instant.now());
        row.setUpdatedBy(actor);
        repository.save(row);
        return vigente();
    }

    private static String t(String s) {
        return s == null ? "" : s.trim();
    }

    private static void bad(String msg) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }
}
