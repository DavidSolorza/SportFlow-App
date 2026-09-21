package com.sportflow.features.sports.application.dto;

import com.sportflow.features.sports.domain.model.ContractStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ContractResponse(
        UUID id,
        UUID jugadorId,
        UUID equipoId,
        String nombreEquipo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Integer numeroCamiseta,
        ContractStatus estado,
        String observaciones
) {}
