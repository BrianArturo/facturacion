package co.smdevs.facturacion.factura.application.service;

import co.smdevs.facturacion.cliente.application.port.out.ClienteRepositoryPort;
import co.smdevs.facturacion.factura.application.port.in.FacturaUseCase;
import co.smdevs.facturacion.factura.application.port.out.FacturaRepositoryPort;
import co.smdevs.facturacion.factura.domain.exception.FacturaInvalidaException;
import co.smdevs.facturacion.factura.domain.exception.FacturaNoEncontradaException;
import co.smdevs.facturacion.factura.domain.model.DetalleFactura;
import co.smdevs.facturacion.factura.domain.model.Factura;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaService implements FacturaUseCase {

    private final FacturaRepositoryPort facturaRepository;
    private final ClienteRepositoryPort clienteRepository;

    public FacturaService(FacturaRepositoryPort facturaRepository,
                          ClienteRepositoryPort clienteRepository) {
        this.facturaRepository = facturaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public Factura crear(Long clienteId, LocalDate fecha, List<DetalleFactura> detalles) {
        if (!clienteRepository.existePorId(clienteId)) {
            throw new FacturaInvalidaException("El cliente con id " + clienteId + " no existe");
        }
        Factura factura = Factura.crear(clienteId, fecha, detalles);
        return facturaRepository.guardar(factura);
    }

    @Override
    public Factura actualizarDetalles(Long facturaId, List<DetalleFactura> detalles) {
        Factura existente = facturaRepository.buscarPorId(facturaId)
                .orElseThrow(() -> new FacturaNoEncontradaException(facturaId));
        return facturaRepository.guardar(existente.actualizarDetalles(detalles));
    }

    @Override
    public void eliminar(Long id) {
        if (!facturaRepository.existePorId(id)) {
            throw new FacturaNoEncontradaException(id);
        }
        facturaRepository.eliminarPorId(id);
    }

    @Override
    public Optional<Factura> buscarPorId(Long id) {
        return facturaRepository.buscarPorId(id);
    }

    @Override
    public List<Factura> listarTodas() {
        return facturaRepository.listarTodas();
    }

    @Override
    public List<Factura> listarPorCliente(Long clienteId) {
        return facturaRepository.listarPorCliente(clienteId);
    }
}
