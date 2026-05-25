package co.smdevs.facturacion.factura.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record DetalleFacturaRequest(
        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @Min(value = 1, message = "La cantidad debe ser mayor a cero")
        int cantidad,

        @NotNull @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
        BigDecimal precioUnitario
) {}
