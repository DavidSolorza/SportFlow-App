package com.sportflow.features.competitions.application.dto;

import com.sportflow.features.competitions.domain.model.RegistrationStatus;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrationResponse(
        UUID id,
        UUID torneoId,
        UUID equipoId,
        String nombreEquipo,
        LocalDate fechaInscripcion,
        RegistrationStatus estado,
        String observaciones
) {}
