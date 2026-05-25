package co.smdevs.facturacion.cliente.infrastructure.adapter.out.persistence;

import co.smdevs.facturacion.cliente.application.port.out.ClienteRepositoryPort;
import co.smdevs.facturacion.cliente.domain.model.Cliente;
import co.smdevs.facturacion.cliente.infrastructure.adapter.out.persistence.mapper.ClienteEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpa;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        var entity = ClienteEntityMapper.toEntity(cliente);
        var guardado = jpa.save(entity);
        return ClienteEntityMapper.toDomain(guardado);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return jpa.findById(id).map(ClienteEntityMapper::toDomain);
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) {
        return jpa.findByEmail(email).map(ClienteEntityMapper::toDomain);
    }

    @Override
    public List<Cliente> listarTodos() {
        return jpa.findAll().stream().map(ClienteEntityMapper::toDomain).toList();
    }

    @Override
    public void eliminarPorId(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existePorId(Long id) {
        return jpa.existsById(id);
    }
}
