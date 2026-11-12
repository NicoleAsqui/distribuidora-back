package ec.distribuidoraguayaquil.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FacturacionCodigoTest {
    @Test
    void codigoAutomaticoDesdeDescripcion() {
        assertEquals("CAJA-CORAZON-25-25-6", FacturacionService.codigoDesde("Caja Corazón 25×25×6"));
        assertEquals("ITEM", FacturacionService.codigoDesde("—"));
    }
}
