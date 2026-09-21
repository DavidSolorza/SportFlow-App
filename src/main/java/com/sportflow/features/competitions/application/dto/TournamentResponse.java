package com.sportflow.features.competitions.application.dto;

import com.sportflow.features.competitions.domain.model.TournamentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record TournamentResponse(
        UUID id,
        UUID deporteId,
        String nombre,
        String descripcion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDate fechaCierreInscripcion,
        int cupoEquipos,
        int equiposInscritos,
        TournamentStatus estado
) {}
