package co.smdevs.facturacion.cliente.infrastructure.adapter.in.web.dto;

import co.smdevs.facturacion.cliente.domain.model.Cliente;

public record ClienteResponse(Long id, String nombre, String email) {

    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNombre(), cliente.getEmail());
    }
}
