package co.smdevs.facturacion.factura.domain.exception;

public class FacturaNoEncontradaException extends RuntimeException {
    public FacturaNoEncontradaException(Long id) {
        super("Factura con id " + id + " no encontrada");
    }
}
