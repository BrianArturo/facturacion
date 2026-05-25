package co.smdevs.facturacion.cliente.domain.exception;

public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String email) {
        super("Ya existe un cliente con el email: " + email);
    }
}
