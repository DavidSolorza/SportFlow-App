package com.sportflow.features.competitions.application.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CreateBracketRequest(
        @NotBlank(message = "El nombre de la llave es obligatorio.")
        String nombre,
        int ronda,
        int orden,
        UUID partidoId
) {}
