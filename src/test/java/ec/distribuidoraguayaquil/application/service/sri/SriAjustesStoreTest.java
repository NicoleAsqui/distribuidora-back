package ec.distribuidoraguayaquil.application.service.sri;

import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.SriAjustesEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.SriAjustesJpaRepository;
import ec.distribuidoraguayaquil.infrastructure.config.SriFacturacionConfig.SriFacturacionProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SriAjustesStoreTest {

    private static SriAjustesStore.Ajustes eternity(String ambiente, String ruc) {
        return new SriAjustesStore.Ajustes(ambiente, 1, ruc, "Asqui Manzaba Nicole Katherine", "ETERNITY EC",
                "Barrio: URB ASTURIAS", "Calle: LA IOJA", "002", "002", "NO", "");
    }

    @Test
    void guardaDesdeElAdminYProtegeElPasoAProduccion() {
        AtomicReference<SriAjustesEntity> db = new AtomicReference<>();
        SriAjustesJpaRepository repo = mock(SriAjustesJpaRepository.class);
        when(repo.findById(any())).thenAnswer(i -> Optional.ofNullable(db.get()));
        when(repo.save(any())).thenAnswer(i -> {
            db.set(i.getArgument(0));
            return i.getArgument(0);
        });
        SriAjustesStore store = new SriAjustesStore(repo, new SriFacturacionProperties(), new ObjectMapper());

        var p = store.guardar(eternity("1", "0953518057001"), false, "admin");
        assertEquals("1", p.getAmbiente());
        assertEquals("002", p.getEmisor().getPuntoEmision());
        assertEquals("ASQUI MANZABA NICOLE KATHERINE", p.getEmisor().getRazonSocial());

        assertThrows(ResponseStatusException.class, () -> store.guardar(eternity("2", "0953518057001"), false, "admin"));
        assertEquals("2", store.guardar(eternity("2", "0953518057001"), true, "admin").getAmbiente());
        assertThrows(ResponseStatusException.class, () -> store.guardar(eternity("1", "095351805"), false, "admin"));
    }
}
