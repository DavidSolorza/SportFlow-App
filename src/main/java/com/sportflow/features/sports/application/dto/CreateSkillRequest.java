package com.sportflow.features.sports.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateSkillRequest(
        @NotBlank(message = "El nombre canónico de la habilidad es obligatorio.")
        String nombreCanonico,
        String descripcion
) {}
