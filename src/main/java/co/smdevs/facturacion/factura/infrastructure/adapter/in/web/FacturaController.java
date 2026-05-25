package co.smdevs.facturacion.factura.infrastructure.adapter.in.web;

import co.smdevs.facturacion.factura.application.port.in.FacturaUseCase;
import co.smdevs.facturacion.factura.domain.exception.FacturaNoEncontradaException;
import co.smdevs.facturacion.factura.domain.model.DetalleFactura;
import co.smdevs.facturacion.factura.infrastructure.adapter.in.web.dto.DetalleFacturaRequest;
import co.smdevs.facturacion.factura.infrastructure.adapter.in.web.dto.FacturaRequest;
import co.smdevs.facturacion.factura.infrastructure.adapter.in.web.dto.FacturaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaUseCase useCase;

    public FacturaController(FacturaUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacturaResponse crear(@Valid @RequestBody FacturaRequest request) {
        var detalles = mapearDetalles(request.detalles());
        var factura = useCase.crear(request.clienteId(), request.fecha(), detalles);
        return FacturaResponse.from(factura);
    }

    @PutMapping("/{id}/detalles")
    public FacturaResponse actualizarDetalles(@PathVariable Long id,
                                              @Valid @RequestBody List<DetalleFacturaRequest> detalles) {
        var factura = useCase.actualizarDetalles(id, mapearDetalles(detalles));
        return FacturaResponse.from(factura);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
    }

    @GetMapping("/{id}")
    public FacturaResponse buscarPorId(@PathVariable Long id) {
        return useCase.buscarPorId(id)
                .map(FacturaResponse::from)
                .orElseThrow(() -> new FacturaNoEncontradaException(id));
    }

    @GetMapping
    public List<FacturaResponse> listar(@RequestParam(required = false) Long clienteId) {
        var facturas = (clienteId == null)
                ? useCase.listarTodas()
                : useCase.listarPorCliente(clienteId);
        return facturas.stream().map(FacturaResponse::from).toList();
    }

    private List<DetalleFactura> mapearDetalles(List<DetalleFacturaRequest> requests) {
        return requests.stream()
                .map(r -> DetalleFactura.crear(r.descripcion(), r.cantidad(), r.precioUnitario()))
                .toList();
    }
}
