package com.sportflow.features.competitions.application.dto;

import com.sportflow.features.competitions.domain.model.PhaseStatus;
import com.sportflow.features.competitions.domain.model.PhaseType;

import java.util.UUID;

public record PhaseResponse(
        UUID id,
        UUID torneoId,
        String nombre,
        PhaseType tipo,
        int orden,
        PhaseStatus estado,
        UUID fasePadreId
) {}
