package ec.distribuidoraguayaquil.domain.port.in;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.RequestStatus;

import java.util.List;

public interface OrderUseCase {
    List<Order> listAll();
    Order getById(String id);
    Order getByCode(String code);
    Order create(Order order);
    Order updateStatus(String id, RequestStatus status);
    /** Adjunta URL del PDF (GCS) al checkoutJson del pedido público. */
    Order attachPdfUrl(String code, String pdfUrl);
}
