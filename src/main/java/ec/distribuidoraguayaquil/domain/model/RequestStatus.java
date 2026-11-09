package ec.distribuidoraguayaquil.domain.model;

public enum RequestStatus {
    PENDING,
    SENT,
    PRODUCTION,
    DELIVERED,
    /** Pagado: entra al calendario con fecha agendada. */
    PAID,
    /** Hecho / entregado (pedidos de la agenda). */
    DONE,
    /** Caducado: pedido pendiente que no se pagó dentro de la validez (1 semana). */
    EXPIRED,
    CANCELED
}
