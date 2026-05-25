package co.smdevs.facturacion.factura.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Raíz del agregado Factura. Contiene N detalles.
 * El total se calcula a partir de los detalles (siempre consistente).
 */
public class Factura {

    private final Long id;
    private final Long clienteId;
    private final LocalDate fecha;
    private final List<DetalleFactura> detalles;

    public Factura(Long id, Long clienteId, LocalDate fecha, List<DetalleFactura> detalles) {
        if (clienteId == null) {
            throw new IllegalArgumentException("La factura debe tener un cliente asociado");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de la factura es obligatoria");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de la factura no puede ser futura");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La factura debe tener al menos un detalle");
        }
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
        this.detalles = List.copyOf(detalles);
    }

    public static Factura crear(Long clienteId, LocalDate fecha, List<DetalleFactura> detalles) {
        return new Factura(null, clienteId, fecha, detalles);
    }

    public Factura conId(Long nuevoId) {
        return new Factura(nuevoId, this.clienteId, this.fecha, this.detalles);
    }

    public Factura actualizarDetalles(List<DetalleFactura> nuevosDetalles) {
        return new Factura(this.id, this.clienteId, this.fecha, nuevosDetalles);
    }

    public BigDecimal calcularTotal() {
        return detalles.stream()
                .map(DetalleFactura::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public LocalDate getFecha() { return fecha; }
    public List<DetalleFactura> getDetalles() { return Collections.unmodifiableList(detalles); }
}
