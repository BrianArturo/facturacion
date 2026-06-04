package co.smdevs.facturacion.shared.config;

import co.smdevs.facturacion.cliente.domain.exception.ClienteNoEncontradoException;
import co.smdevs.facturacion.cliente.domain.exception.EmailDuplicadoException;
import co.smdevs.facturacion.factura.domain.exception.FacturaInvalidaException;
import co.smdevs.facturacion.factura.domain.exception.FacturaNoEncontradaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("notFound devuelve 404 para ClienteNoEncontradoException")
    void notFoundParaCliente() {
        var response = handler.notFound(new ClienteNoEncontradoException(1L));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("error");
    }

    @Test
    @DisplayName("notFound devuelve 404 para FacturaNoEncontradaException")
    void notFoundParaFactura() {
        var response = handler.notFound(new FacturaNoEncontradaException(2L));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody()).containsKey("status");
    }

    @Test
    @DisplayName("conflict devuelve 409 para EmailDuplicadoException")
    void conflictParaEmailDuplicado() {
        var response = handler.conflict(new EmailDuplicadoException("dup@test.com"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsKey("error");
    }

    @Test
    @DisplayName("badRequest devuelve 400 para IllegalArgumentException")
    void badRequestParaIllegalArgument() {
        var response = handler.badRequest(new IllegalArgumentException("dato inválido"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("status", 400);
    }

    @Test
    @DisplayName("badRequest devuelve 400 para FacturaInvalidaException")
    void badRequestParaFacturaInvalida() {
        var response = handler.badRequest(new FacturaInvalidaException("factura sin detalles"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsKey("error");
    }
}