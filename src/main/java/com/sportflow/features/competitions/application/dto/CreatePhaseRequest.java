package com.sportflow.features.competitions.application.dto;

import com.sportflow.features.competitions.domain.model.PhaseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreatePhaseRequest(
        @NotBlank(message = "El nombre de la fase es obligatorio.")
        String nombre,
        @NotNull(message = "El tipo de fase es obligatorio.")
        PhaseType tipo,
        int orden,
        UUID fasePadreId
) {}
