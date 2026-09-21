package com.sportflow.features.sports.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreateContractRequest(
        @NotNull(message = "El identificador del equipo es obligatorio.")
        UUID equipoId,
        @NotNull(message = "La fecha de inicio es obligatoria.")
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer numeroCamiseta,
        String observaciones
) {}
