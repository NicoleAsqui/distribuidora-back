package ec.distribuidoraguayaquil.application.service;

import ec.distribuidoraguayaquil.domain.model.Order;
import ec.distribuidoraguayaquil.domain.model.OrderItem;
import ec.distribuidoraguayaquil.domain.port.out.OrderRepositoryPort;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.DisenoEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.entity.catalog.VarianteEntity;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.OrderEventJpaRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.DisenoRepository;
import ec.distribuidoraguayaquil.infrastructure.adapter.out.persistence.repository.catalog.VarianteRepository;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Pedidos antiguos sin sección: se completa desde el catálogo (SKU → diseño → sección). */
class OrderSeccionLookupTest {
    @Test
    void completaSeccionDesdeElCatalogo() {
        Order viejo = new Order("o1", "PED-1017", "Patricia", null, null, "", null,
                List.of(new OrderItem("SKU-MDF", "Caja Desayuno", "35×35×6 cm", "Crudo", BigDecimal.ONE, 3, null),
                        new OrderItem("MANUAL", "Algo a mano", "", "", BigDecimal.ONE, 1, null)),
                BigDecimal.TEN, ec.distribuidoraguayaquil.domain.model.RequestStatus.PAID, Instant.now());
        OrderRepositoryPort repo = mock(OrderRepositoryPort.class);
        when(repo.findById("o1")).thenReturn(Optional.of(viejo));
        VarianteRepository variantes = mock(VarianteRepository.class);
        DisenoRepository disenos = mock(DisenoRepository.class);
        VarianteEntity v = new VarianteEntity();
        v.setDisenoId(5L);
        DisenoEntity d = new DisenoEntity();
        d.setSeccion("mdf");
        when(variantes.findBySku(anyString())).thenReturn(Optional.empty());
        when(variantes.findBySku("SKU-MDF")).thenReturn(Optional.of(v));
        when(disenos.findById(5L)).thenReturn(Optional.of(d));
        OrderService svc = new OrderService(repo, mock(OrderEventJpaRepository.class), new ObjectMapper(), variantes, disenos);
        Order o = svc.getById("o1");
        assertEquals("mdf", o.items().get(0).seccion());
        assertNull(o.items().get(1).seccion());
    }
}
