package com.sportflow.features.sports.application.dto;

import java.util.UUID;

public record SkillResponse(
        UUID id,
        String nombreCanonico,
        String descripcion,
        boolean activo
) {}
