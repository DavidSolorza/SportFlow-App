package com.sportflow.features.competitions.application.dto;

import com.sportflow.features.competitions.domain.model.EventType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RecordMatchEventRequest(
        UUID jugadorId,
        @NotNull(message = "El identificador del equipo es obligatorio.")
        UUID equipoId,
        @NotNull(message = "El tipo de evento es obligatorio.")
        EventType tipoEvento,
        @Min(value = 0, message = "El minuto debe ser mayor o igual a 0.")
        @Max(value = 150, message = "El minuto no puede exceder 150.")
        int minuto,
        String descripcion
) {}
