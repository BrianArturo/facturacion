package co.smdevs.facturacion.cliente.domain.exception;

public class ClienteNoEncontradoException extends RuntimeException {
    public ClienteNoEncontradoException(Long id) {
        super("Cliente con id " + id + " no encontrado");
    }
}
