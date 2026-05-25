package co.smdevs.facturacion.cliente.application.port.out;

import co.smdevs.facturacion.cliente.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (driven). Define el contrato de persistencia desde la perspectiva del dominio.
 * Lo implementa la capa de infraestructura (adaptador JPA).
 */
public interface ClienteRepositoryPort {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(Long id);

    Optional<Cliente> buscarPorEmail(String email);

    List<Cliente> listarTodos();

    void eliminarPorId(Long id);

    boolean existePorId(Long id);
}
