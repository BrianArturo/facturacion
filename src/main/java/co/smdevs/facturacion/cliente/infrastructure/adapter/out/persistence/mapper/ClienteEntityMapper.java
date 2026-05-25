package co.smdevs.facturacion.cliente.infrastructure.adapter.out.persistence.mapper;

import co.smdevs.facturacion.cliente.domain.model.Cliente;
import co.smdevs.facturacion.cliente.infrastructure.adapter.out.persistence.entity.ClienteEntity;

public final class ClienteEntityMapper {

    private ClienteEntityMapper() {}

    public static ClienteEntity toEntity(Cliente cliente) {
        return new ClienteEntity(cliente.getId(), cliente.getNombre(), cliente.getEmail());
    }

    public static Cliente toDomain(ClienteEntity entity) {
        return new Cliente(entity.getId(), entity.getNombre(), entity.getEmail());
    }
}
