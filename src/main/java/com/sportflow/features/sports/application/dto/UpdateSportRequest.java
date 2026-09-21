package com.sportflow.features.sports.application.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record UpdateSportRequest(
        @NotBlank(message = "El nombre canónico del deporte es obligatorio.")
        String nombreCanonico,
        String descripcion,
        Boolean activo,
        UUID deportePadreId
) {}
