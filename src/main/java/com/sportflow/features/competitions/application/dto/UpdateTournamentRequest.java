package com.sportflow.features.competitions.application.dto;

import com.sportflow.features.competitions.domain.model.TournamentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record UpdateTournamentRequest(
        @NotBlank(message = "El nombre del torneo es obligatorio.")
        String nombre,
        String descripcion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDate fechaCierreInscripcion,
        @Min(value = 2, message = "El cupo debe ser de al menos 2 equipos.")
        Integer cupoEquipos,
        TournamentStatus estado
) {}
