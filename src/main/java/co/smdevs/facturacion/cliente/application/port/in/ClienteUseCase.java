package co.smdevs.facturacion.cliente.application.port.in;

import co.smdevs.facturacion.cliente.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de entrada (driver). Define los casos de uso disponibles para el cliente.
 * Lo implementa la capa de aplicación, lo invoca la capa de infraestructura (REST).
 */
public interface ClienteUseCase {

    Cliente crear(String nombre, String email);

    Cliente actualizar(Long id, String nombre, String email);

    void eliminar(Long id);

    Optional<Cliente> buscarPorId(Long id);

    List<Cliente> listarTodos();
}
