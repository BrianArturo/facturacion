package co.smdevs.facturacion.factura.application.port.out;

import co.smdevs.facturacion.factura.domain.model.Factura;

import java.util.List;
import java.util.Optional;

public interface FacturaRepositoryPort {

    Factura guardar(Factura factura);

    Optional<Factura> buscarPorId(Long id);

    List<Factura> listarTodas();

    List<Factura> listarPorCliente(Long clienteId);

    void eliminarPorId(Long id);

    boolean existePorId(Long id);
}
