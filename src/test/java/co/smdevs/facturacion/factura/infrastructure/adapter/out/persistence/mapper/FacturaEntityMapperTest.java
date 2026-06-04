package co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence.mapper;

import co.smdevs.facturacion.factura.domain.model.DetalleFactura;
import co.smdevs.facturacion.factura.domain.model.Factura;
import co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence.entity.DetalleFacturaEntity;
import co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence.entity.FacturaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FacturaEntityMapper")
class FacturaEntityMapperTest {

    private static final LocalDate FECHA = LocalDate.of(2025, 1, 15);

    @Test
    @DisplayName("toEntity convierte factura con detalles correctamente")
    void toEntityMapeaCampos() {
        var detalle = new DetalleFactura(10L, "Producto A", 2, new BigDecimal("50.00"));
        var factura = new Factura(1L, 5L, FECHA, List.of(detalle));

        FacturaEntity entity = FacturaEntityMapper.toEntity(factura);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getClienteId()).isEqualTo(5L);
        assertThat(entity.getFecha()).isEqualTo(FECHA);
        assertThat(entity.getDetalles()).hasSize(1);
        assertThat(entity.getDetalles().get(0).getDescripcion()).isEqualTo("Producto A");
        assertThat(entity.getDetalles().get(0).getCantidad()).isEqualTo(2);
        assertThat(entity.getDetalles().get(0).getPrecioUnitario()).isEqualByComparingTo("50.00");
    }

    @Test
    @DisplayName("toDomain convierte entidad con detalles correctamente")
    void toDomainMapeaCampos() {
        var detalleEntity = new DetalleFacturaEntity(10L, "Servicio B", 3, new BigDecimal("100.00"));
        var entity = new FacturaEntity(2L, 7L, FECHA, List.of(detalleEntity));

        Factura factura = FacturaEntityMapper.toDomain(entity);

        assertThat(factura.getId()).isEqualTo(2L);
        assertThat(factura.getClienteId()).isEqualTo(7L);
        assertThat(factura.getFecha()).isEqualTo(FECHA);
        assertThat(factura.getDetalles()).hasSize(1);
        assertThat(factura.getDetalles().get(0).getDescripcion()).isEqualTo("Servicio B");
        assertThat(factura.getDetalles().get(0).getCantidad()).isEqualTo(3);
        assertThat(factura.getDetalles().get(0).getPrecioUnitario()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("toDomain y toEntity son inversas entre sí")
    void roundTrip() {
        var detalle = new DetalleFactura(1L, "Item", 1, new BigDecimal("10.00"));
        var original = new Factura(1L, 1L, FECHA, List.of(detalle));

        Factura resultado = FacturaEntityMapper.toDomain(FacturaEntityMapper.toEntity(original));

        assertThat(resultado.getId()).isEqualTo(original.getId());
        assertThat(resultado.getClienteId()).isEqualTo(original.getClienteId());
        assertThat(resultado.getFecha()).isEqualTo(original.getFecha());
        assertThat(resultado.getDetalles()).hasSize(1);
        assertThat(resultado.getDetalles().get(0).getDescripcion()).isEqualTo("Item");
    }
}