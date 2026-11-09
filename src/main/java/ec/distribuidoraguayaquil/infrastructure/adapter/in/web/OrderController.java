package ec.distribuidoraguayaquil.infrastructure.adapter.in.web;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.OrderEvent;
import ec.distribuidoraguayaquil.domain.model.OrderItem;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;
import ec.distribuidoraguayaquil.application.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ec.distribuidoraguayaquil.domain.port.in.AuthUseCase;
import ec.distribuidoraguayaquil.domain.port.in.OrderUseCase;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.AttachOrderPdfRequest;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.CreateOrderRequest;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.OrderAgendaRequests;
import ec.distribuidoraguayaquil.infrastructure.adapter.in.web.dto.StatusUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Pedidos. No hay DELETE: un pedido nunca se elimina, solo cambia de estado (Cancelado incluido).
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderUseCase orderUseCase;
    private final AuthUseCase authUseCase;

    public OrderController(OrderUseCase orderUseCase, AuthUseCase authUseCase) {
        this.orderUseCase = orderUseCase;
        this.authUseCase = authUseCase;
    }

    @GetMapping
    public List<Order> list() {
        return orderUseCase.listAll();
    }

    /** Público: página de gracias / resumen por código PED-… */
    @GetMapping("/public/{code}")
    public Order getPublicByCode(@PathVariable String code) {
        return orderUseCase.getByCode(code);
    }

    /** Público: adjunta URL del PDF subido a GCS tras generar la cotización. */
    @PutMapping("/public/{code}/pdf")
    public Order attachPdf(@PathVariable String code, @Valid @RequestBody AttachOrderPdfRequest request) {
        return orderUseCase.attachPdfUrl(code, request.pdfUrl());
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable String id) {
        return orderUseCase.getById(id);
    }

    /** Público: pedido desde el carrito web. */
    @PostMapping
    public Order create(@Valid @RequestBody CreateOrderRequest request) {
        return orderUseCase.create(toOrder(request));
    }

    /** Admin: pedido ingresado a mano. */
    @PostMapping("/manual")
    public Order createManual(@Valid @RequestBody CreateOrderRequest request, HttpServletRequest http) {
        return orderUseCase.createManual(toOrder(request), actor(http));
    }

    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request,
                              HttpServletRequest http) {
        String who = actor(http);
        // Al pasar a Pagado se guardan los datos de la guía (cédula y paquetes).
        if (request.cedula() != null || request.packages() != null) {
            orderUseCase.updateShipping(id, null, null, request.cedula(), request.packages(), null, null, null, who);
        }
        // La cédula es opcional (a veces el cliente aún no la envía); el admin ve un aviso si falta.
        return orderUseCase.updateStatus(id, request.status(), request.scheduledAt(), request.deposit(),
                request.note(), who);
    }

    @PutMapping("/{id}/schedule")
    public Order reschedule(@PathVariable String id, @Valid @RequestBody OrderAgendaRequests.Reschedule request,
                            HttpServletRequest http) {
        return orderUseCase.reschedule(id, request.scheduledAt(), request.reason(), actor(http));
    }

    /** Admin: datos de la guía (nombres, apellidos, cédula, paquetes, provincia, ciudad). */
    @PutMapping("/{id}/shipping")
    public Order updateShipping(@PathVariable String id, @RequestBody OrderAgendaRequests.Shipping request,
                                HttpServletRequest http) {
        String who = actor(http);
        if (request.phone() != null || request.email() != null) {
            orderUseCase.updateContact(id, request.phone(), request.email(), who);
        }
        return orderUseCase.updateShipping(id, request.firstName(), request.lastName(), request.cedula(),
                request.packages(), request.province(), request.city(), request.note(), who);
    }

    /** Admin: el cliente cambió de retiro a envío (o al revés) / nueva dirección. */
    @PutMapping("/{id}/delivery")
    public Order updateDelivery(@PathVariable String id, @RequestBody OrderAgendaRequests.Delivery request,
                                HttpServletRequest http) {
        return orderUseCase.updateDelivery(id, request.fulfillmentMethod(), request.city(), request.address(),
                request.references(), request.mapsUrl(), request.note(), actor(http));
    }

    @PostMapping("/{id}/payments")
    public Order addPayment(@PathVariable String id, @Valid @RequestBody OrderAgendaRequests.Payment request,
                            HttpServletRequest http) {
        return orderUseCase.addPayment(id, request.amount(), request.note(), actor(http));
    }

    @GetMapping("/{id}/events")
    public List<OrderEvent> events(@PathVariable String id) {
        return orderUseCase.events(id);
    }

    @PostMapping("/{id}/events")
    public OrderEvent addEvent(@PathVariable String id, @RequestBody OrderAgendaRequests.Event request,
                               HttpServletRequest http) {
        return orderUseCase.addEvent(id, request.type(), request.message(), actor(http));
    }

    private Order toOrder(CreateOrderRequest request) {
        List<OrderItem> items = request.items().stream()
                .map(i -> new OrderItem(i.productRef(), i.name(), i.size(), i.color(), i.price(), i.qty(), i.image(),
                        i.seccion() == null || i.seccion().isBlank() ? null : i.seccion().trim().toLowerCase()))
                .toList();
        return new Order(
                null, null, request.clientName(), request.clientEmail(), request.clientPhone(),
                request.notes(), request.checkoutJson(), items, null, null, null);
    }

    /** Nombre de quien hizo el cambio (usuario del admin, con mayúscula inicial). */
    private String actor(HttpServletRequest http) {
        String auth = http.getHeader("Authorization");
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : http.getHeader("X-Admin-Token");
        String user = authUseCase.usernameFromToken(token);
        if (user == null || user.isBlank()) {
            return "Admin";
        }
        return Character.toUpperCase(user.charAt(0)) + user.substring(1);
    }
}
