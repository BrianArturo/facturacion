package co.smdevs.facturacion.factura.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object: línea de detalle de una factura.
 * Inmutable y calcula su propio subtotal.
 */
public class DetalleFactura {

    private final Long id;
    private final String descripcion;
    private final int cantidad;
    private final BigDecimal precioUnitario;

    public DetalleFactura(Long id, String descripcion, int cantidad, BigDecimal precioUnitario) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del detalle es obligatoria");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
        }
        this.id = id;
        this.descripcion = descripcion.trim();
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario.setScale(2, RoundingMode.HALF_UP);
    }

    public static DetalleFactura crear(String descripcion, int cantidad, BigDecimal precioUnitario) {
        return new DetalleFactura(null, descripcion, cantidad, precioUnitario);
    }

    public BigDecimal calcularSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetalleFactura that)) return false;
        return cantidad == that.cantidad
                && Objects.equals(descripcion, that.descripcion)
                && Objects.equals(precioUnitario, that.precioUnitario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descripcion, cantidad, precioUnitario);
    }
}
