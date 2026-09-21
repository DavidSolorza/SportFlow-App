package com.sportflow.features.sports.application.dto;

import java.time.Instant;
import java.util.UUID;

public record SportResponse(
        UUID id,
        String nombreCanonico,
        String descripcion,
        boolean activo,
        UUID deportePadreId,
        Instant creadoEn,
        Instant actualizadoEn
) {}
