package co.smdevs.facturacion.factura.infrastructure.adapter.in.web;

import co.smdevs.facturacion.factura.application.port.in.FacturaUseCase;
import co.smdevs.facturacion.factura.domain.model.DetalleFactura;
import co.smdevs.facturacion.factura.domain.model.Factura;
import co.smdevs.facturacion.shared.config.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacturaController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("FacturaController")
class FacturaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FacturaUseCase useCase;

    private static Factura facturaEjemplo() {
        var detalle = new DetalleFactura(1L, "Producto", 2, new BigDecimal("50.00"));
        return new Factura(1L, 5L, LocalDate.of(2025, 1, 15), List.of(detalle));
    }

    @Nested
    @DisplayName("POST /api/facturas")
    class Crear {

        @Test
        @DisplayName("201 con la factura creada")
        void crea() throws Exception {
            when(useCase.crear(eq(5L), eq(LocalDate.of(2025, 1, 15)), anyList()))
                    .thenReturn(facturaEjemplo());

            mvc.perform(post("/api/facturas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "clienteId": 5,
                                      "fecha": "2025-01-15",
                                      "detalles": [
                                        {"descripcion":"Producto","cantidad":2,"precioUnitario":50.00}
                                      ]
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.clienteId").value(5))
                    .andExpect(jsonPath("$.detalles.length()").value(1))
                    .andExpect(jsonPath("$.total").value(100.00));
        }

        @Test
        @DisplayName("400 cuando la lista de detalles está vacía")
        void validacionDetallesVacios() throws Exception {
            mvc.perform(post("/api/facturas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "clienteId": 5,
                                      "fecha": "2025-01-15",
                                      "detalles": []
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(useCase);
        }

        @Test
        @DisplayName("400 cuando falta el clienteId")
        void validacionClienteIdNulo() throws Exception {
            mvc.perform(post("/api/facturas")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "fecha": "2025-01-15",
                                      "detalles": [
                                        {"descripcion":"X","cantidad":1,"precioUnitario":10.00}
                                      ]
                                    }
                                    """))
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(useCase);
        }
    }

    @Nested
    @DisplayName("PUT /api/facturas/{id}/detalles")
    class ActualizarDetalles {

        @Test
        @DisplayName("200 con los detalles actualizados")
        void actualiza() throws Exception {
            when(useCase.actualizarDetalles(eq(1L), anyList()))
                    .thenReturn(facturaEjemplo());

            mvc.perform(put("/api/facturas/1/detalles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    [{"descripcion":"Producto","cantidad":2,"precioUnitario":50.00}]
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }
    }

    @Nested
    @DisplayName("DELETE /api/facturas/{id}")
    class Eliminar {

        @Test
        @DisplayName("204 al eliminar una factura existente")
        void elimina() throws Exception {
            doNothing().when(useCase).eliminar(1L);

            mvc.perform(delete("/api/facturas/1"))
                    .andExpect(status().isNoContent());

            verify(useCase).eliminar(1L);
        }
    }

    @Nested
    @DisplayName("GET /api/facturas/{id}")
    class BuscarPorId {

        @Test
        @DisplayName("200 cuando la factura existe")
        void retornaFactura() throws Exception {
            when(useCase.buscarPorId(1L)).thenReturn(Optional.of(facturaEjemplo()));

            mvc.perform(get("/api/facturas/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("404 cuando la factura no existe")
        void retorna404() throws Exception {
            when(useCase.buscarPorId(99L)).thenReturn(Optional.empty());

            mvc.perform(get("/api/facturas/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/facturas")
    class Listar {

        @Test
        @DisplayName("200 con todas las facturas cuando no se filtra por cliente")
        void listaTodas() throws Exception {
            when(useCase.listarTodas()).thenReturn(List.of(facturaEjemplo()));

            mvc.perform(get("/api/facturas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("200 con facturas filtradas por clienteId")
        void listaPorCliente() throws Exception {
            when(useCase.listarPorCliente(5L)).thenReturn(List.of(facturaEjemplo()));

            mvc.perform(get("/api/facturas").param("clienteId", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(useCase).listarPorCliente(5L);
            verify(useCase, never()).listarTodas();
        }
    }
}