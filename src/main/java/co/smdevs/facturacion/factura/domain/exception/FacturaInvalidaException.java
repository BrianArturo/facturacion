package co.smdevs.facturacion.factura.domain.exception;

public class FacturaInvalidaException extends RuntimeException {
    public FacturaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
