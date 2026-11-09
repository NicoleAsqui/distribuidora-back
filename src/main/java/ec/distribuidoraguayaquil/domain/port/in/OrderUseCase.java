package ec.distribuidoraguayaquil.domain.port.in;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.OrderEvent;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Pedidos. Nunca se eliminan: solo cambian de estado (Cancelado incluido) y todo queda en el historial.
 */
public interface OrderUseCase {
    List<Order> listAll();
    Order getById(String id);
    Order getByCode(String code);
    /** Pedido desde el carrito web (público). */
    Order create(Order order);
    /** Pedido ingresado a mano desde el admin. */
    Order createManual(Order order, String actor);

    /** Pedido creado desde una cotización aceptada; conserva el total exacto de la cotización. */
    Order createFromQuote(Order order, java.math.BigDecimal quoteTotal, String actor, String quoteCode);

    /**
     * Cambia el estado. Al pasar a PAID exige fecha agendada (scheduledAt) y puede registrar un abono inicial.
     */
    Order updateStatus(String id, RequestStatus status, Instant scheduledAt, BigDecimal deposit, String note,
                       String actor);

    /** Aplaza / cambia la fecha agendada (queda en el historial con el motivo). */
    Order reschedule(String id, Instant scheduledAt, String reason, String actor);

    /** Registra un abono (anticipo o saldo). */
    Order addPayment(String id, BigDecimal amount, String note, String actor);

    /** Nota libre o recordatorio en el historial. */
    OrderEvent addEvent(String id, String type, String message, String actor);

    List<OrderEvent> events(String id);

    /** Adjunta URL del PDF (GCS) al checkoutJson del pedido público. */
    Order attachPdfUrl(String code, String pdfUrl);

    /** Datos para la guía de envío: nombres/apellidos, cédula, paquetes, provincia, ciudad. */
    Order updateShipping(String id, String firstName, String lastName, String cedula, Integer packages,
                         String province, String city, String note, String actor);

    /**
     * Cambia la forma de entrega (retiro ↔ envío) y la dirección. Recalcula el total
     * (retiro tiene 3% de descuento) salvo en pedidos que vienen de cotización (total pactado).
     */
    Order updateDelivery(String id, String fulfillmentMethod, String city, String address, String references,
                         String mapsUrl, String note, String actor);
}
