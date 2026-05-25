package co.smdevs.facturacion.factura.application;

import co.smdevs.facturacion.cliente.application.port.out.ClienteRepositoryPort;
import co.smdevs.facturacion.factura.application.port.out.FacturaRepositoryPort;
import co.smdevs.facturacion.factura.application.service.FacturaService;
import co.smdevs.facturacion.factura.domain.exception.FacturaInvalidaException;
import co.smdevs.facturacion.factura.domain.exception.FacturaNoEncontradaException;
import co.smdevs.facturacion.factura.domain.model.DetalleFactura;
import co.smdevs.facturacion.factura.domain.model.Factura;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacturaService")
class FacturaServiceTest {

    @Mock
    private FacturaRepositoryPort facturaRepository;

    @Mock
    private ClienteRepositoryPort clienteRepository;

    @InjectMocks
    private FacturaService service;

    private List<DetalleFactura> detallesValidos;

    @BeforeEach
    void setUp() {
        detallesValidos = List.of(
                DetalleFactura.crear("Café", 2, new BigDecimal("4500.00")),
                DetalleFactura.crear("Pan", 5, new BigDecimal("1500.00"))
        );
    }

    @Nested
    @DisplayName("Crear")
    class Crear {

        @Test
        @DisplayName("debe crear factura cuando el cliente existe")
        void deberiaCrearFactura() {
            when(clienteRepository.existePorId(1L)).thenReturn(true);
            when(facturaRepository.guardar(any(Factura.class))).thenAnswer(inv -> {
                Factura f = inv.getArgument(0);
                return f.conId(100L);
            });

            var creada = service.crear(1L, LocalDate.now(), detallesValidos);

            ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
            verify(facturaRepository).guardar(captor.capture());

            assertThat(creada.getId()).isEqualTo(100L);
            assertThat(captor.getValue().getClienteId()).isEqualTo(1L);
            assertThat(captor.getValue().calcularTotal()).isEqualByComparingTo("16500.00");
        }

        @Test
        @DisplayName("debe rechazar la creación si el cliente no existe")
        void deberiaRechazarSiClienteNoExiste() {
            when(clienteRepository.existePorId(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.crear(99L, LocalDate.now(), detallesValidos))
                    .isInstanceOf(FacturaInvalidaException.class)
                    .hasMessageContaining("99");

            verify(facturaRepository, never()).guardar(any());
        }

        @Test
        @DisplayName("debe propagar errores de dominio (lista vacía)")
        void deberiaPropagarErrorDominio() {
            when(clienteRepository.existePorId(1L)).thenReturn(true);

            assertThatThrownBy(() -> service.crear(1L, LocalDate.now(), List.of()))
                    .isInstanceOf(IllegalArgumentException.class);

            verify(facturaRepository, never()).guardar(any());
        }
    }

    @Nested
    @DisplayName("Actualizar detalles")
    class ActualizarDetalles {

        @Test
        @DisplayName("debe reemplazar los detalles de una factura existente")
        void deberiaActualizarDetalles() {
            var existente = new Factura(5L, 1L, LocalDate.now(), detallesValidos);
            when(facturaRepository.buscarPorId(5L)).thenReturn(Optional.of(existente));
            when(facturaRepository.guardar(any(Factura.class))).thenAnswer(inv -> inv.getArgument(0));

            var nuevosDetalles = List.of(DetalleFactura.crear("Té", 1, new BigDecimal("3000.00")));

            var actualizada = service.actualizarDetalles(5L, nuevosDetalles);

            assertThat(actualizada.getId()).isEqualTo(5L);
            assertThat(actualizada.getDetalles()).hasSize(1);
            assertThat(actualizada.calcularTotal()).isEqualByComparingTo("3000.00");
        }

        @Test
        @DisplayName("debe fallar si la factura no existe")
        void deberiaFallarSiFacturaNoExiste() {
            when(facturaRepository.buscarPorId(404L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarDetalles(404L, detallesValidos))
                    .isInstanceOf(FacturaNoEncontradaException.class);

            verify(facturaRepository, never()).guardar(any());
        }
    }

    @Nested
    @DisplayName("Eliminar")
    class Eliminar {

        @Test
        @DisplayName("debe eliminar cuando la factura existe")
        void deberiaEliminar() {
            when(facturaRepository.existePorId(1L)).thenReturn(true);

            service.eliminar(1L);

            verify(facturaRepository).eliminarPorId(1L);
        }

        @Test
        @DisplayName("debe fallar cuando no existe")
        void deberiaFallarSiNoExiste() {
            when(facturaRepository.existePorId(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.eliminar(99L))
                    .isInstanceOf(FacturaNoEncontradaException.class);

            verify(facturaRepository, never()).eliminarPorId(any());
        }
    }

    @Nested
    @DisplayName("Consultas")
    class Consultas {

        @Test
        @DisplayName("buscarPorId debe delegar")
        void buscarPorId() {
            var f = new Factura(1L, 1L, LocalDate.now(), detallesValidos);
            when(facturaRepository.buscarPorId(1L)).thenReturn(Optional.of(f));

            assertThat(service.buscarPorId(1L)).contains(f);
        }

        @Test
        @DisplayName("listarPorCliente debe delegar y devolver la lista")
        void listarPorCliente() {
            var f = new Factura(1L, 7L, LocalDate.now(), detallesValidos);
            when(facturaRepository.listarPorCliente(7L)).thenReturn(List.of(f));

            assertThat(service.listarPorCliente(7L)).containsExactly(f);
        }

        @Test
        @DisplayName("listarTodas debe delegar")
        void listarTodas() {
            when(facturaRepository.listarTodas()).thenReturn(List.of());
            assertThat(service.listarTodas()).isEmpty();
        }
    }
}
