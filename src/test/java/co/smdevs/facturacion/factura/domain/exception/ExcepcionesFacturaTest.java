package co.smdevs.facturacion.factura.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Excepciones de Factura")
class ExcepcionesFacturaTest {

    @Test
    @DisplayName("FacturaNoEncontradaException incluye el id en el mensaje")
    void facturaNoEncontradaIncluyeId() {
        var ex = new FacturaNoEncontradaException(99L);
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).contains("99");
    }

    @Test
    @DisplayName("FacturaInvalidaException preserva el mensaje recibido")
    void facturaInvalidaPreservaMensaje() {
        var ex = new FacturaInvalidaException("detalle inválido");
        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("detalle inválido");
    }
}