package co.smdevs.facturacion.cliente.infrastructure.adapter.in.web;

import co.smdevs.facturacion.cliente.application.port.in.ClienteUseCase;
import co.smdevs.facturacion.cliente.domain.exception.ClienteNoEncontradoException;
import co.smdevs.facturacion.cliente.infrastructure.adapter.in.web.dto.ClienteRequest;
import co.smdevs.facturacion.cliente.infrastructure.adapter.in.web.dto.ClienteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteUseCase useCase;

    public ClienteController(ClienteUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(@Valid @RequestBody ClienteRequest request) {
        var cliente = useCase.crear(request.nombre(), request.email());
        return ClienteResponse.from(cliente);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        var cliente = useCase.actualizar(id, request.nombre(), request.email());
        return ClienteResponse.from(cliente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        return useCase.buscarPorId(id)
                .map(ClienteResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return useCase.listarTodos().stream().map(ClienteResponse::from).toList();
    }
}
