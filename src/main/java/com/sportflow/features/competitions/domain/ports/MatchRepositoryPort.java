package com.sportflow.features.competitions.domain.ports;

import com.sportflow.features.competitions.domain.model.Match;
import com.sportflow.features.competitions.domain.model.MatchStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRepositoryPort {
    Match save(Match match);
    Optional<Match> findById(UUID id);
    List<Match> findByPhaseId(UUID phaseId);
    List<Match> findByGroupId(UUID groupId);
    List<Match> findByPhaseIdAndEstado(UUID phaseId, MatchStatus estado);
    List<Match> findByPartidoOrigen(UUID partidoId);
    void deleteById(UUID id);
}
