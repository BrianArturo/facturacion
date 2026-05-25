package co.smdevs.facturacion.factura.infrastructure.adapter.in.web.dto;

import co.smdevs.facturacion.factura.domain.model.Factura;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FacturaResponse(
        Long id,
        Long clienteId,
        LocalDate fecha,
        List<DetalleFacturaResponse> detalles,
        BigDecimal total
) {
    public static FacturaResponse from(Factura f) {
        List<DetalleFacturaResponse> detalles = f.getDetalles().stream()
                .map(DetalleFacturaResponse::from)
                .toList();
        return new FacturaResponse(f.getId(), f.getClienteId(), f.getFecha(), detalles, f.calcularTotal());
    }
}
