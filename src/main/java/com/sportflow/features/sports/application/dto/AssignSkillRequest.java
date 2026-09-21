package com.sportflow.features.sports.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignSkillRequest(
        @NotNull(message = "El identificador de la habilidad es obligatorio.")
        UUID habilidadId,
        String nivel,
        String observacion
) {}
