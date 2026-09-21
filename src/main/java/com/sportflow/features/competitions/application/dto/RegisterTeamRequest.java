package com.sportflow.features.competitions.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RegisterTeamRequest(
        @NotNull(message = "El identificador del equipo es obligatorio.")
        UUID equipoId,
        String observaciones
) {}
