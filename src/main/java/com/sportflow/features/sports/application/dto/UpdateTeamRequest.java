package com.sportflow.features.sports.application.dto;

import com.sportflow.features.sports.domain.model.TeamGender;
import com.sportflow.features.sports.domain.model.TeamStatus;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record UpdateTeamRequest(
        @NotBlank(message = "El nombre distintivo del equipo es obligatorio.")
        String nombreDistintivo,
        @NotBlank(message = "La ciudad es obligatoria.")
        String ciudad,
        @NotBlank(message = "La categoría es obligatoria.")
        String categoria,
        TeamGender genero,
        TeamStatus estado,
        UUID clubId
) {}
