package com.sportflow.features.competitions.application.dto;

import jakarta.validation.constraints.Min;

public record RecordMatchResultRequest(
        @Min(value = 0, message = "Los goles del equipo local no pueden ser negativos.")
        int golesLocal,
        @Min(value = 0, message = "Los goles del equipo visitante no pueden ser negativos.")
        int golesVisitante,
        String confirmadoPor,
        String observaciones
) {}
