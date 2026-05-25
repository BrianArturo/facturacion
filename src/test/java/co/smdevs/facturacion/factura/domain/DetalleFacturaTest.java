package co.smdevs.facturacion.factura.domain;

import co.smdevs.facturacion.factura.domain.model.DetalleFactura;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Value Object DetalleFactura")
class DetalleFacturaTest {

    @Test
    @DisplayName("debe crear un detalle válido y calcular subtotal correctamente")
    void deberiaCalcularSubtotal() {
        var detalle = DetalleFactura.crear("Café", 3, new BigDecimal("4500.00"));

        assertThat(detalle.calcularSubtotal()).isEqualByComparingTo("13500.00");
    }

    @Test
    @DisplayName("debe normalizar precio unitario a 2 decimales")
    void deberiaNormalizarEscala() {
        var detalle = DetalleFactura.crear("Producto", 2, new BigDecimal("10.555"));

        assertThat(detalle.getPrecioUnitario()).isEqualByComparingTo("10.56");
        assertThat(detalle.calcularSubtotal()).isEqualByComparingTo("21.12");
    }

    @ParameterizedTest(name = "cantidad inválida: {0}")
    @ValueSource(ints = {0, -1, -100})
    @DisplayName("debe rechazar cantidades menores o iguales a cero")
    void deberiaRechazarCantidadesInvalidas(int cantidad) {
        assertThatThrownBy(() -> DetalleFactura.crear("X", cantidad, BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cantidad");
    }

    @ParameterizedTest(name = "precio inválido: {0}")
    @ValueSource(strings = {"0", "-1", "-99.99"})
    @DisplayName("debe rechazar precios menores o iguales a cero")
    void deberiaRechazarPreciosInvalidos(String precio) {
        assertThatThrownBy(() -> DetalleFactura.crear("X", 1, new BigDecimal(precio)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");
    }

    @Test
    @DisplayName("debe rechazar descripción nula")
    void deberiaRechazarDescripcionNula() {
        assertThatThrownBy(() -> DetalleFactura.crear(null, 1, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("debe rechazar precio nulo")
    void deberiaRechazarPrecioNulo() {
        assertThatThrownBy(() -> DetalleFactura.crear("X", 1, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("dos detalles con mismos valores son iguales")
    void deberianSerIgualesPorValor() {
        var d1 = DetalleFactura.crear("Café", 2, new BigDecimal("100.00"));
        var d2 = DetalleFactura.crear("Café", 2, new BigDecimal("100.00"));

        assertThat(d1).isEqualTo(d2).hasSameHashCodeAs(d2);
    }
}
