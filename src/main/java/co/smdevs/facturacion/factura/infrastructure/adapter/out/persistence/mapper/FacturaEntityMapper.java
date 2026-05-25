package co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence.mapper;

import co.smdevs.facturacion.factura.domain.model.DetalleFactura;
import co.smdevs.facturacion.factura.domain.model.Factura;
import co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence.entity.DetalleFacturaEntity;
import co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence.entity.FacturaEntity;

import java.util.List;

public final class FacturaEntityMapper {

    private FacturaEntityMapper() {}

    public static FacturaEntity toEntity(Factura factura) {
        List<DetalleFacturaEntity> detalles = factura.getDetalles().stream()
                .map(d -> new DetalleFacturaEntity(d.getId(), d.getDescripcion(), d.getCantidad(), d.getPrecioUnitario()))
                .toList();
        return new FacturaEntity(factura.getId(), factura.getClienteId(), factura.getFecha(), detalles);
    }

    public static Factura toDomain(FacturaEntity entity) {
        List<DetalleFactura> detalles = entity.getDetalles().stream()
                .map(d -> new DetalleFactura(d.getId(), d.getDescripcion(), d.getCantidad(), d.getPrecioUnitario()))
                .toList();
        return new Factura(entity.getId(), entity.getClienteId(), entity.getFecha(), detalles);
    }
}
