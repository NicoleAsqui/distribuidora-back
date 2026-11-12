package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.FacturacionService;
import ec.distribuidoraguayaquil.domain.port.in.AuthUseCase;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.EmitirFacturaAdminRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/facturacion")
public class FacturacionAdminController {

    private final FacturacionService facturacionService;
    private final AuthUseCase authUseCase;

    public FacturacionAdminController(FacturacionService facturacionService, AuthUseCase authUseCase) {
        this.facturacionService = facturacionService;
        this.authUseCase = authUseCase;
    }

    @GetMapping("/config")
    public Map<String, Object> config() {
        return facturacionService.publicConfig();
    }

    @GetMapping("/facturas")
    public List<Map<String, Object>> list(@RequestParam(required = false) String orderId) {
        return facturacionService.list(orderId);
    }

    @GetMapping("/facturas/{id}")
    public Map<String, Object> get(@PathVariable String id) {
        return facturacionService.get(id);
    }

    @PostMapping("/emitir")
    public Map<String, Object> emitir(@Valid @RequestBody EmitirFacturaAdminRequest request, HttpServletRequest http) {
        return facturacionService.emitir(request, actor(http));
    }

    @PostMapping("/facturas/{id}/consultar")
    public Map<String, Object> consultar(@PathVariable String id) {
        return facturacionService.reconsultar(id);
    }

    public record EntregarRequest(String rideUrl, String pdfBase64, Boolean enviarEmail) {}

    @PostMapping("/facturas/{id}/entregar")
    public Map<String, Object> entregar(@PathVariable String id, @RequestBody EntregarRequest body) {
        return facturacionService.entregar(id, body.rideUrl(), body.pdfBase64(), Boolean.TRUE.equals(body.enviarEmail()));
    }

    @GetMapping(value = "/facturas/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> xml(@PathVariable String id) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"factura-" + id + ".xml\"")
                .body(facturacionService.xmlAutorizado(id));
    }

    private String actor(HttpServletRequest http) {
        String auth = http.getHeader("Authorization");
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : http.getHeader("X-Admin-Token");
        String user = authUseCase.usernameFromToken(token);
        return user == null || user.isBlank() ? "Admin" : user;
    }
}
