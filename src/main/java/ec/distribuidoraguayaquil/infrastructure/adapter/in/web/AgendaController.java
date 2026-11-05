package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.application.service.AgendaService;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.OrderAgendaRequests;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Calendario del taller.
 * - /api/admin/agenda/** y /api/admin/staff/** → requieren token admin (interceptor).
 * - /api/agenda/public/{token}/** → link privado para trabajadores (sin precios; acciones con PIN).
 */
@RestController
public class AgendaController {

    private final AgendaService agenda;

    public AgendaController(AgendaService agenda) {
        this.agenda = agenda;
    }

    // ---------- Admin ----------

    @GetMapping("/api/admin/agenda/link")
    public Map<String, String> link() {
        return Map.of("token", agenda.currentToken());
    }

    @PostMapping("/api/admin/agenda/link/rotate")
    public Map<String, String> rotate() {
        return Map.of("token", agenda.rotateToken());
    }

    @GetMapping("/api/admin/staff")
    public List<AgendaService.StaffDto> staff() {
        return agenda.listStaff();
    }

    @PostMapping("/api/admin/staff")
    public AgendaService.StaffDto createStaff(@RequestBody OrderAgendaRequests.StaffUpsert body) {
        return agenda.createStaff(body.nombre(), body.pin());
    }

    @PutMapping("/api/admin/staff/{id}")
    public AgendaService.StaffDto updateStaff(@PathVariable Long id, @RequestBody OrderAgendaRequests.StaffUpsert body) {
        return agenda.updateStaff(id, body.nombre(), body.pin(), body.activo());
    }

    // ---------- Público (link privado) ----------

    @GetMapping("/api/agenda/public/{token}")
    public List<AgendaService.AgendaEntryDto> entries(@PathVariable String token) {
        return agenda.publicEntries(token);
    }

    @GetMapping("/api/agenda/public/{token}/orders/{id}")
    public AgendaService.AgendaEntryDto entry(@PathVariable String token, @PathVariable String id) {
        return agenda.publicEntry(token, id);
    }

    @PostMapping("/api/agenda/public/{token}/orders/{id}/done")
    public AgendaService.AgendaEntryDto done(@PathVariable String token, @PathVariable String id,
                                             @RequestBody OrderAgendaRequests.WorkerDone body,
                                             HttpServletRequest http) {
        return agenda.markDone(token, id, body.pin(), body.note(), clientKey(http));
    }

    @PostMapping("/api/agenda/public/{token}/orders/{id}/reschedule")
    public AgendaService.AgendaEntryDto reschedule(@PathVariable String token, @PathVariable String id,
                                                   @Valid @RequestBody OrderAgendaRequests.WorkerReschedule body,
                                                   HttpServletRequest http) {
        return agenda.reschedule(token, id, body.pin(), body.scheduledAt(), body.reason(), clientKey(http));
    }

    private static String clientKey(HttpServletRequest http) {
        String fwd = http.getHeader("X-Forwarded-For");
        if (fwd != null && !fwd.isBlank()) {
            return fwd.split(",")[0].trim();
        }
        return http.getRemoteAddr();
    }
}
