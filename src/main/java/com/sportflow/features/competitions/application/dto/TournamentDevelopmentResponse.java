package com.sportflow.features.competitions.application.dto;

import com.sportflow.features.competitions.domain.model.TournamentStatus;

import java.util.List;
import java.util.UUID;

public record TournamentDevelopmentResponse(
        UUID torneoId,
        String nombreTorneo,
        TournamentStatus estado,
        List<PhaseDevelopmentDTO> fases
) {
    public record PhaseDevelopmentDTO(
            UUID faseId,
            String nombreFase,
            String tipoFase,
            int orden,
            String estado,
            List<GroupDevelopmentDTO> grupos,
            List<BracketResponse> llaves,
            List<MatchResponse> partidos
    ) {}

    public record GroupDevelopmentDTO(
            UUID grupoId,
            String nombreGrupo,
            List<StandingsResponse> tablaPosiciones
    ) {}
}
