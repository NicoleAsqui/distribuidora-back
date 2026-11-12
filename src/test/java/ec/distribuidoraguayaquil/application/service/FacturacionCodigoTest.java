package ec.distribuidoraguayaquil.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FacturacionCodigoTest {
    @Test
    void mismoProductoMismaClaveAunqueCambienTildesSignosYMedidas() {
        String a = FacturacionService.clave("Caja Corazón · Cartulina · 25×25×6 cm");
        assertEquals("CAJA-CORAZON-CARTULINA-25-25-6", a);
        assertEquals(a, FacturacionService.clave("caja corazon - cartulina - 25x25x6"));
        assertEquals(a, FacturacionService.clave("CAJA CORAZON CARTULINA 25 X 25 X 6 CM"));
        assertNotEquals(a, FacturacionService.clave("Caja Corazón · Cartulina · 25×25×8 cm"));
    }
}
