package com.sportflow.features.sports.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record PlayerSkillResponse(
        UUID habilidadId,
        String nombreHabilidad,
        String nivel,
        String observacion,
        LocalDate fechaRegistro
) {}
