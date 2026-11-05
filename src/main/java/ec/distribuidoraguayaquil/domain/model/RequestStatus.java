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
    CANCELED
}
