package co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence;

import co.smdevs.facturacion.factura.infrastructure.adapter.out.persistence.entity.FacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacturaJpaRepository extends JpaRepository<FacturaEntity, Long> {
    List<FacturaEntity> findByClienteId(Long clienteId);
}
