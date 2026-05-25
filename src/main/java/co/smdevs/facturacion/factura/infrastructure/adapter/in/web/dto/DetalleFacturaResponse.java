package co.smdevs.facturacion.factura.infrastructure.adapter.in.web.dto;

import co.smdevs.facturacion.factura.domain.model.DetalleFactura;

import java.math.BigDecimal;

public record DetalleFacturaResponse(
        Long id,
        String descripcion,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
    public static DetalleFacturaResponse from(DetalleFactura d) {
        return new DetalleFacturaResponse(
                d.getId(), d.getDescripcion(), d.getCantidad(), d.getPrecioUnitario(), d.calcularSubtotal());
    }
}
