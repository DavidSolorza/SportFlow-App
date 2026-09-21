package com.sportflow.features.sports.application.dto;

import java.util.List;

public record PlayerProfileResponse(
        PlayerResponse jugador,
        ContractResponse equipoActual,
        List<ContractResponse> historialTraspasos,
        List<PlayerSkillResponse> habilidades
) {}
