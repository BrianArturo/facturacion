package co.smdevs.facturacion.cliente.application;

import co.smdevs.facturacion.cliente.application.port.out.ClienteRepositoryPort;
import co.smdevs.facturacion.cliente.application.service.ClienteService;
import co.smdevs.facturacion.cliente.domain.exception.ClienteNoEncontradoException;
import co.smdevs.facturacion.cliente.domain.exception.EmailDuplicadoException;
import co.smdevs.facturacion.cliente.domain.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort repository;

    @InjectMocks
    private ClienteService service;

    private Cliente clienteExistente;

    @BeforeEach
    void setUp() {
        clienteExistente = new Cliente(1L, "Brian", "brian@smdevs.co");
    }

    @Nested
    @DisplayName("Crear")
    class Crear {

        @Test
        @DisplayName("debe guardar y devolver el cliente cuando el email no existe")
        void deberiaCrearCliente() {
            when(repository.buscarPorEmail("nuevo@correo.com")).thenReturn(Optional.empty());
            when(repository.guardar(any(Cliente.class))).thenAnswer(inv -> {
                Cliente c = inv.getArgument(0);
                return c.conId(10L);
            });

            var creado = service.crear("Cliente Nuevo", "nuevo@correo.com");

            assertThat(creado.getId()).isEqualTo(10L);
            assertThat(creado.getEmail()).isEqualTo("nuevo@correo.com");
            verify(repository).buscarPorEmail("nuevo@correo.com");
            verify(repository).guardar(any(Cliente.class));
        }

        @Test
        @DisplayName("debe lanzar EmailDuplicadoException cuando el email ya existe")
        void deberiaFallarSiEmailExiste() {
            when(repository.buscarPorEmail("brian@smdevs.co")).thenReturn(Optional.of(clienteExistente));

            assertThatThrownBy(() -> service.crear("Otro", "brian@smdevs.co"))
                    .isInstanceOf(EmailDuplicadoException.class)
                    .hasMessageContaining("brian@smdevs.co");

            verify(repository, never()).guardar(any());
        }

        @Test
        @DisplayName("debe propagar IllegalArgumentException si el dominio rechaza los datos")
        void deberiaPropagarErrorDeDominio() {
            assertThatThrownBy(() -> service.crear("", "x@y.com"))
                    .isInstanceOf(IllegalArgumentException.class);

            verifyNoInteractions(repository);
        }
    }

    @Nested
    @DisplayName("Actualizar")
    class Actualizar {

        @Test
        @DisplayName("debe actualizar nombre y email cuando todo es válido")
        void deberiaActualizarCliente() {
            when(repository.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente));
            when(repository.buscarPorEmail("nuevo@x.com")).thenReturn(Optional.empty());
            when(repository.guardar(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

            var actualizado = service.actualizar(1L, "Brian M.", "nuevo@x.com");

            ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
            verify(repository).guardar(captor.capture());

            assertThat(actualizado.getNombre()).isEqualTo("Brian M.");
            assertThat(actualizado.getEmail()).isEqualTo("nuevo@x.com");
            assertThat(captor.getValue().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("no debe validar email duplicado cuando el email no cambia")
        void noDeberiaValidarEmailSiNoCambia() {
            when(repository.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente));
            when(repository.guardar(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

            service.actualizar(1L, "Brian Nuevo", "brian@smdevs.co");

            verify(repository, never()).buscarPorEmail(any());
        }

        @Test
        @DisplayName("debe lanzar ClienteNoEncontradoException si el id no existe")
        void deberiaFallarSiClienteNoExiste() {
            when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizar(99L, "X", "x@x.com"))
                    .isInstanceOf(ClienteNoEncontradoException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("debe lanzar EmailDuplicadoException si el nuevo email pertenece a otro cliente")
        void deberiaFallarSiNuevoEmailEstaEnUso() {
            when(repository.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente));
            when(repository.buscarPorEmail("otro@x.com"))
                    .thenReturn(Optional.of(new Cliente(2L, "Otro", "otro@x.com")));

            assertThatThrownBy(() -> service.actualizar(1L, "Brian", "otro@x.com"))
                    .isInstanceOf(EmailDuplicadoException.class);

            verify(repository, never()).guardar(any());
        }
    }

    @Nested
    @DisplayName("Eliminar")
    class Eliminar {

        @Test
        @DisplayName("debe eliminar cuando el cliente existe")
        void deberiaEliminarCliente() {
            when(repository.existePorId(1L)).thenReturn(true);

            service.eliminar(1L);

            verify(repository).eliminarPorId(1L);
        }

        @Test
        @DisplayName("debe lanzar excepción cuando el cliente no existe")
        void deberiaFallarAlEliminarInexistente() {
            when(repository.existePorId(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.eliminar(99L))
                    .isInstanceOf(ClienteNoEncontradoException.class);

            verify(repository, never()).eliminarPorId(any());
        }
    }

    @Nested
    @DisplayName("Consultas")
    class Consultas {

        @Test
        @DisplayName("buscarPorId debe delegar al repositorio")
        void buscarPorIdDeberiaDelegar() {
            when(repository.buscarPorId(1L)).thenReturn(Optional.of(clienteExistente));

            assertThat(service.buscarPorId(1L)).contains(clienteExistente);
        }

        @Test
        @DisplayName("listarTodos debe devolver la lista del repositorio")
        void listarTodosDeberiaDelegar() {
            when(repository.listarTodos()).thenReturn(List.of(clienteExistente));

            assertThat(service.listarTodos()).containsExactly(clienteExistente);
        }
    }
}
