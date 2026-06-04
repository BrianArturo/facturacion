package co.smdevs.facturacion.cliente.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Excepciones de Cliente")
class ExcepcionesClienteTest {

    @Test
    @DisplayName("ClienteNoEncontradoException incluye el id en el mensaje")
    void clienteNoEncontradoIncluideId() {
        var ex = new ClienteNoEncontradoException(42L);
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).contains("42");
    }

    @Test
    @DisplayName("EmailDuplicadoException incluye el email en el mensaje")
    void emailDuplicadoIncluyeEmail() {
        var ex = new EmailDuplicadoException("dup@test.com");
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).contains("dup@test.com");
    }
}
