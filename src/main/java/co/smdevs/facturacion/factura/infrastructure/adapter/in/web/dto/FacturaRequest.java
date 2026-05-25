package co.smdevs.facturacion.factura.infrastructure.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record FacturaRequest(
        @NotNull(message = "El clienteId es obligatorio")
        Long clienteId,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @NotEmpty(message = "Debe incluir al menos un detalle")
        @Valid
        List<DetalleFacturaRequest> detalles
) {}
