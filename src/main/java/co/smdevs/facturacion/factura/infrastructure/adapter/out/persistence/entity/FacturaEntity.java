package co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facturas")
public class FacturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private LocalDate fecha;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleFacturaEntity> detalles = new ArrayList<>();

    public FacturaEntity() {}

    public FacturaEntity(Long id, Long clienteId, LocalDate fecha, List<DetalleFacturaEntity> detalles) {
        this.id = id;
        this.clienteId = clienteId;
        this.fecha = fecha;
        if (detalles != null) {
            detalles.forEach(d -> d.setFactura(this));
            this.detalles = detalles;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public List<DetalleFacturaEntity> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleFacturaEntity> detalles) {
        this.detalles.clear();
        if (detalles != null) {
            detalles.forEach(d -> d.setFactura(this));
            this.detalles.addAll(detalles);
        }
    }
}
