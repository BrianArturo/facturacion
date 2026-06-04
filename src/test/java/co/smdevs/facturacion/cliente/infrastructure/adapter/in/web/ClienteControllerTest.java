package co.smdevs.facturacion.cliente.infrastructure.adapter.in.web;

import co.smdevs.facturacion.cliente.application.port.in.ClienteUseCase;
import co.smdevs.facturacion.cliente.domain.exception.ClienteNoEncontradoException;
import co.smdevs.facturacion.cliente.domain.model.Cliente;
import co.smdevs.facturacion.shared.config.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ClienteController")
class ClienteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @MockBean
    private ClienteUseCase useCase;

    @Nested
    @DisplayName("POST /api/clientes")
    class Crear {

        @Test
        @DisplayName("201 con el cliente creado cuando los datos son válidos")
        void crea() throws Exception {
            when(useCase.crear("Brian", "brian@test.com"))
                    .thenReturn(new Cliente(1L, "Brian", "brian@test.com"));

            mvc.perform(post("/api/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre":"Brian","email":"brian@test.com"}
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombre").value("Brian"))
                    .andExpect(jsonPath("$.email").value("brian@test.com"));
        }

        @Test
        @DisplayName("400 cuando falta el nombre")
        void validacionNombreVacio() throws Exception {
            mvc.perform(post("/api/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre":"","email":"brian@test.com"}
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(useCase);
        }

        @Test
        @DisplayName("400 cuando el email no es válido")
        void validacionEmailInvalido() throws Exception {
            mvc.perform(post("/api/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre":"Brian","email":"no-es-email"}
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(useCase);
        }
    }

    @Nested
    @DisplayName("PUT /api/clientes/{id}")
    class Actualizar {

        @Test
        @DisplayName("200 con el cliente actualizado")
        void actualiza() throws Exception {
            when(useCase.actualizar(1L, "Brian M.", "brian@test.com"))
                    .thenReturn(new Cliente(1L, "Brian M.", "brian@test.com"));

            mvc.perform(put("/api/clientes/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"nombre":"Brian M.","email":"brian@test.com"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombre").value("Brian M."));
        }
    }

    @Nested
    @DisplayName("DELETE /api/clientes/{id}")
    class Eliminar {

        @Test
        @DisplayName("204 cuando se elimina correctamente")
        void elimina() throws Exception {
            doNothing().when(useCase).eliminar(1L);

            mvc.perform(delete("/api/clientes/1"))
                    .andExpect(status().isNoContent());

            verify(useCase).eliminar(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/clientes/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("200 con el cliente cuando existe")
        void retornaCliente() throws Exception {
            when(useCase.buscarPorId(1L))
                    .thenReturn(Optional.of(new Cliente(1L, "Brian", "brian@test.com")));

            mvc.perform(get("/api/clientes/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("404 cuando el cliente no existe")
        void retorna404() throws Exception {
            when(useCase.buscarPorId(99L)).thenReturn(Optional.empty());

            mvc.perform(get("/api/clientes/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/clientes")
    class Listar {

        @Test
        @DisplayName("200 con la lista de clientes")
        void listaClientes() throws Exception {
            when(useCase.listarTodos()).thenReturn(List.of(
                    new Cliente(1L, "Brian", "brian@test.com"),
                    new Cliente(2L, "Ana", "ana@test.com")
            ));

            mvc.perform(get("/api/clientes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("200 con lista vacía cuando no hay clientes")
        void listaVacia() throws Exception {
            when(useCase.listarTodos()).thenReturn(List.of());

            mvc.perform(get("/api/clientes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }
}