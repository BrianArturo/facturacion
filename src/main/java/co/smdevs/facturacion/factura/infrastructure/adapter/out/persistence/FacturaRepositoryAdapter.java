package co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence;

import co.smdevs.facturacion.factura.application.port.out.FacturaRepositoryPort;
import co.smdevs.facturacion.factura.domain.model.Factura;
import co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence.mapper.FacturaEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FacturaRepositoryAdapter implements FacturaRepositoryPort {

    private final FacturaJpaRepository jpa;

    public FacturaRepositoryAdapter(FacturaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Factura guardar(Factura factura) {
        var entity = FacturaEntityMapper.toEntity(factura);

        if (factura.getId() != null) {
            // Update: cargamos la entidad gestionada y le reemplazamos los detalles
            // (orphanRemoval=true en FacturaEntity elimina los detalles antiguos).
            var existente = jpa.findById(factura.getId())
                    .orElseThrow(() -> new IllegalStateException("Factura desaparecida: " + factura.getId()));
            existente.setFecha(entity.getFecha());
            existente.setClienteId(entity.getClienteId());
            existente.setDetalles(entity.getDetalles());
            return FacturaEntityMapper.toDomain(jpa.save(existente));
        }

        return FacturaEntityMapper.toDomain(jpa.save(entity));
    }

    @Override
    public Optional<Factura> buscarPorId(Long id) {
        return jpa.findById(id).map(FacturaEntityMapper::toDomain);
    }

    @Override
    public List<Factura> listarTodas() {
        return jpa.findAll().stream().map(FacturaEntityMapper::toDomain).toList();
    }

    @Override
    public List<Factura> listarPorCliente(Long clienteId) {
        return jpa.findByClienteId(clienteId).stream().map(FacturaEntityMapper::toDomain).toList();
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
