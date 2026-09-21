package com.sportflow.features.sports.application.dto;

import java.time.LocalDate;

public record TerminateContractRequest(
        LocalDate fechaFin,
        String motivo
) {}
