package ec.distribuidoraguayaquil.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuotePdfStorageServiceTest {

    @Test
    void matchesOnlyFilesOfThatCode() {
        assertTrue(QuotePdfStorageService.matchesCode("cot-1023-maria_lopez-ab12cd34-ef56.pdf", "COT-1023"));
        assertTrue(QuotePdfStorageService.matchesCode("cotizacion-ped-1017-1731300000000-ab12cd34-ef56.pdf", "PED-1017"));
        assertTrue(QuotePdfStorageService.matchesCode("ped-1017-cliente-personalizada-ab12cd34-ef56.pdf", "PED-1017"));
        assertFalse(QuotePdfStorageService.matchesCode("cot-10230-otro-ab12cd34-ef56.pdf", "COT-1023"));
        assertFalse(QuotePdfStorageService.matchesCode("cot-102-otro-ab12cd34-ef56.pdf", "COT-1023"));
        assertFalse(QuotePdfStorageService.matchesCode("cualquier-cosa.pdf", ""));
    }
}
