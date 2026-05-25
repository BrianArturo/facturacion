package co.smdevs.facturacion.cliente.application.service;

import co.smdevs.facturacion.cliente.application.port.in.ClienteUseCase;
import co.smdevs.facturacion.cliente.application.port.out.ClienteRepositoryPort;
import co.smdevs.facturacion.cliente.domain.exception.ClienteNoEncontradoException;
import co.smdevs.facturacion.cliente.domain.exception.EmailDuplicadoException;
import co.smdevs.facturacion.cliente.domain.model.Cliente;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService implements ClienteUseCase {

    private final ClienteRepositoryPort repository;

    public ClienteService(ClienteRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Cliente crear(String nombre, String email) {
        Cliente nuevo = Cliente.crear(nombre, email);
        repository.buscarPorEmail(nuevo.getEmail()).ifPresent(c -> {
            throw new EmailDuplicadoException(nuevo.getEmail());
        });
        return repository.guardar(nuevo);
    }

    @Override
    public Cliente actualizar(Long id, String nombre, String email) {
        Cliente existente = repository.buscarPorId(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));

        Cliente actualizado = existente.actualizar(nombre, email);

        // Si el email cambió, validar que no esté en uso por otro cliente
        if (!existente.getEmail().equals(actualizado.getEmail())) {
            repository.buscarPorEmail(actualizado.getEmail()).ifPresent(c -> {
                throw new EmailDuplicadoException(actualizado.getEmail());
            });
        }

        return repository.guardar(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existePorId(id)) {
            throw new ClienteNoEncontradoException(id);
        }
        repository.eliminarPorId(id);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    @Override
    public List<Cliente> listarTodos() {
        return repository.listarTodos();
    }
}
