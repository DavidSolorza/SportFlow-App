package com.sportflow.features.competitions.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreateTournamentRequest(
        @NotBlank(message = "El nombre del torneo es obligatorio.")
        String nombre,
        String descripcion,
        @NotNull(message = "El identificador del deporte es obligatorio.")
        UUID deporteId,
        @NotNull(message = "La fecha de inicio es obligatoria.")
        LocalDate fechaInicio,
        @NotNull(message = "La fecha de fin es obligatoria.")
        LocalDate fechaFin,
        @NotNull(message = "La fecha de cierre de inscripciones es obligatoria.")
        LocalDate fechaCierreInscripcion,
        @Min(value = 2, message = "El cupo debe ser de al menos 2 equipos.")
        int cupoEquipos
) {}
