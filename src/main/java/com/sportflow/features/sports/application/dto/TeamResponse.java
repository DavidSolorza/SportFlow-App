package com.sportflow.features.sports.application.dto;

import com.sportflow.features.sports.domain.model.TeamGender;
import com.sportflow.features.sports.domain.model.TeamStatus;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record TeamResponse(
        UUID id,
        UUID clubId,
        String nombreDistintivo,
        String ciudad,
        String categoria,
        TeamGender genero,
        TeamStatus estado,
        LocalDate fechaInscripcion,
        Set<UUID> deporteIds
) {}
