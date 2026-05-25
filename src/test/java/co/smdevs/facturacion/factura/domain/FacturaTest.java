package co.smdevs.facturacion.factura.domain;

import co.smdevs.facturacion.factura.domain.model.DetalleFactura;
import co.smdevs.facturacion.factura.domain.model.Factura;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Agregado Factura")
class FacturaTest {

    private DetalleFactura detalle(int cantidad, String precio) {
        return DetalleFactura.crear("Producto", cantidad, new BigDecimal(precio));
    }

    @Nested
    @DisplayName("Creación")
    class Creacion {

        @Test
        @DisplayName("debe crear una factura válida")
        void deberiaCrearFacturaValida() {
            var factura = Factura.crear(1L, LocalDate.now(), List.of(detalle(2, "100.00")));

            assertThat(factura.getId()).isNull();
            assertThat(factura.getClienteId()).isEqualTo(1L);
            assertThat(factura.getDetalles()).hasSize(1);
        }

        @Test
        @DisplayName("debe rechazar facturas sin clienteId")
        void deberiaRechazarSinCliente() {
            assertThatThrownBy(() -> Factura.crear(null, LocalDate.now(), List.of(detalle(1, "10"))))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cliente");
        }

        @Test
        @DisplayName("debe rechazar fechas futuras")
        void deberiaRechazarFechaFutura() {
            var fechaFutura = LocalDate.now().plusDays(1);

            assertThatThrownBy(() -> Factura.crear(1L, fechaFutura, List.of(detalle(1, "10"))))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("futura");
        }

        @Test
        @DisplayName("debe rechazar facturas sin detalles")
        void deberiaRechazarSinDetalles() {
            assertThatThrownBy(() -> Factura.crear(1L, LocalDate.now(), List.of()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("detalle");
        }

        @Test
        @DisplayName("debe rechazar detalles nulos")
        void deberiaRechazarDetallesNulos() {
            assertThatThrownBy(() -> Factura.crear(1L, LocalDate.now(), null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("debe rechazar fecha nula")
        void deberiaRechazarFechaNula() {
            assertThatThrownBy(() -> Factura.crear(1L, null, List.of(detalle(1, "10"))))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Cálculo de total")
    class CalculoTotal {

        @Test
        @DisplayName("debe sumar correctamente los subtotales de varios detalles")
        void deberiaSumarSubtotales() {
            var factura = Factura.crear(1L, LocalDate.now(), List.of(
                    detalle(2, "100.00"),    // 200.00
                    detalle(3, "50.50"),     // 151.50
                    detalle(1, "999.99")     // 999.99
            ));

            assertThat(factura.calcularTotal()).isEqualByComparingTo("1351.49");
        }

        @Test
        @DisplayName("total con un solo detalle debe igualar a su subtotal")
        void totalConUnDetalle() {
            var factura = Factura.crear(1L, LocalDate.now(), List.of(detalle(5, "1000.00")));

            assertThat(factura.calcularTotal()).isEqualByComparingTo("5000.00");
        }
    }

    @Nested
    @DisplayName("Inmutabilidad")
    class Inmutabilidad {

        @Test
        @DisplayName("getDetalles debe devolver lista inmutable")
        void detallesInmutables() {
            var factura = Factura.crear(1L, LocalDate.now(), List.of(detalle(1, "10")));

            assertThatThrownBy(() -> factura.getDetalles().add(detalle(1, "20")))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("conId debe devolver nueva instancia con id")
        void conIdGeneraNueva() {
            var f = Factura.crear(1L, LocalDate.now(), List.of(detalle(1, "10")));

            var conId = f.conId(99L);

            assertThat(f.getId()).isNull();
            assertThat(conId.getId()).isEqualTo(99L);
        }

        @Test
        @DisplayName("actualizarDetalles debe devolver nueva factura con nuevos detalles y mismo id/cliente")
        void actualizarDetallesGeneraNueva() {
            var original = new Factura(5L, 1L, LocalDate.now(), List.of(detalle(1, "10")));

            var actualizada = original.actualizarDetalles(List.of(detalle(2, "50"), detalle(1, "30")));

            assertThat(actualizada.getId()).isEqualTo(5L);
            assertThat(actualizada.getClienteId()).isEqualTo(1L);
            assertThat(actualizada.getDetalles()).hasSize(2);
            assertThat(actualizada.calcularTotal()).isEqualByComparingTo("130.00");
            assertThat(original.getDetalles()).hasSize(1); // sin mutar
        }
    }
}
