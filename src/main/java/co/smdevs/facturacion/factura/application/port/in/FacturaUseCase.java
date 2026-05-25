package co.smdevs.facturacion.factura.application.port.in;

import co.smdevs.facturacion.factura.domain.model.DetalleFactura;
import co.smdevs.facturacion.factura.domain.model.Factura;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FacturaUseCase {

    Factura crear(Long clienteId, LocalDate fecha, List<DetalleFactura> detalles);

    Factura actualizarDetalles(Long facturaId, List<DetalleFactura> detalles);

    void eliminar(Long id);

    Optional<Factura> buscarPorId(Long id);

    List<Factura> listarTodas();

    List<Factura> listarPorCliente(Long clienteId);
}
