package co.smdevs.facturacion.cliente.domain;

import co.smdevs.facturacion.cliente.domain.model.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Modelo de dominio Cliente")
class ClienteTest {

    @Nested
    @DisplayName("Creación")
    class Creacion {

        @Test
        @DisplayName("debe crear un cliente válido")
        void deberiaCrearClienteValido() {
            var cliente = Cliente.crear("Brian Maldonado", "brian@smdevs.co");

            assertThat(cliente.getId()).isNull();
            assertThat(cliente.getNombre()).isEqualTo("Brian Maldonado");
            assertThat(cliente.getEmail()).isEqualTo("brian@smdevs.co");
        }

        @Test
        @DisplayName("debe normalizar email a minúsculas y aplicar trim al nombre")
        void deberiaNormalizarEmailYNombre() {
            var cliente = Cliente.crear("  Brian  ", "  BRIAN@SMDEVS.CO  ");

            assertThat(cliente.getNombre()).isEqualTo("Brian");
            assertThat(cliente.getEmail()).isEqualTo("brian@smdevs.co");
        }

        @ParameterizedTest(name = "nombre inválido: [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "A", "  X  "})
        @DisplayName("debe rechazar nombres inválidos")
        void deberiaRechazarNombresInvalidos(String nombreInvalido) {
            assertThatThrownBy(() -> Cliente.crear(nombreInvalido, "ok@correo.com"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("nombre");
        }

        @ParameterizedTest(name = "email inválido: [{0}]")
        @ValueSource(strings = {"no-es-email", "sin@dominio", "@dominio.com", "espacio @x.com"})
        @DisplayName("debe rechazar emails con formato inválido")
        void deberiaRechazarEmailsInvalidos(String emailInvalido) {
            assertThatThrownBy(() -> Cliente.crear("Brian", emailInvalido))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("email");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("debe rechazar email nulo o vacío")
        void deberiaRechazarEmailNuloOVacio(String email) {
            assertThatThrownBy(() -> Cliente.crear("Brian", email))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("email");
        }
    }

    @Nested
    @DisplayName("Comportamiento inmutable")
    class Inmutabilidad {

        @Test
        @DisplayName("conId debe devolver una nueva instancia con id asignado sin mutar la original")
        void conIdDeberiaDevolverNuevaInstancia() {
            var original = Cliente.crear("Brian", "brian@smdevs.co");

            var conId = original.conId(42L);

            assertThat(original.getId()).isNull();
            assertThat(conId.getId()).isEqualTo(42L);
            assertThat(conId.getEmail()).isEqualTo(original.getEmail());
        }

        @Test
        @DisplayName("actualizar debe devolver una nueva instancia con los datos modificados")
        void actualizarDeberiaDevolverNuevaInstancia() {
            var original = new Cliente(7L, "Brian", "brian@smdevs.co");

            var actualizado = original.actualizar("Brian M.", "brian.m@smdevs.co");

            assertThat(actualizado.getId()).isEqualTo(7L);
            assertThat(actualizado.getNombre()).isEqualTo("Brian M.");
            assertThat(actualizado.getEmail()).isEqualTo("brian.m@smdevs.co");
            assertThat(original.getNombre()).isEqualTo("Brian"); // no mutado
        }

        @Test
        @DisplayName("actualizar con email inválido debe fallar y no afectar al original")
        void actualizarConDatosInvalidosDeberiaFallar() {
            var original = new Cliente(1L, "Brian", "brian@smdevs.co");

            assertThatThrownBy(() -> original.actualizar("Brian", "email-malo"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThat(original.getEmail()).isEqualTo("brian@smdevs.co");
        }
    }

    @Nested
    @DisplayName("Igualdad por id y email")
    class Igualdad {

        @Test
        @DisplayName("dos clientes con mismo id y email deben ser iguales")
        void deberianSerIgualesConMismoIdYEmail() {
            var c1 = new Cliente(1L, "Brian", "brian@smdevs.co");
            var c2 = new Cliente(1L, "Otro nombre", "brian@smdevs.co");

            assertThat(c1).isEqualTo(c2).hasSameHashCodeAs(c2);
        }

        @Test
        @DisplayName("clientes con distinto email no son iguales")
        void deberianSerDistintosConEmailDiferente() {
            var c1 = new Cliente(1L, "Brian", "a@x.com");
            var c2 = new Cliente(1L, "Brian", "b@x.com");

            assertThat(c1).isNotEqualTo(c2);
        }
    }
}
