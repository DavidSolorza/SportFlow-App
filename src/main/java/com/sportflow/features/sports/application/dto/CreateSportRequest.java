package com.sportflow.features.sports.application.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateSportRequest(
        @NotBlank(message = "El nombre canónico del deporte es obligatorio.")
        String nombreCanonico,
        String descripcion,
        UUID deportePadreId
) {}
