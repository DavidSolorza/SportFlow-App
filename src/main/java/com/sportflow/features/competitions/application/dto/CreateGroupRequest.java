package com.sportflow.features.competitions.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupRequest(
        @NotBlank(message = "El nombre del grupo es obligatorio.")
        String nombre,
        int orden
) {}
