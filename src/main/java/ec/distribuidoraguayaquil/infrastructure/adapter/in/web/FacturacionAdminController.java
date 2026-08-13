package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.FacturacionService;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.EmitirFacturaAdminRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/facturacion")
public class FacturacionAdminController {

    private final FacturacionService facturacionService;

    public FacturacionAdminController(FacturacionService facturacionService) {
        this.facturacionService = facturacionService;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        return facturacionService.publicConfig();
    }

    @PostMapping("/emitir")
    public Map<String, Object> emitir(@Valid @RequestBody EmitirFacturaAdminRequest request) {
        return facturacionService.emitir(request);
    }
}
