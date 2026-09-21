package com.sportflow.features.competitions.domain.ports;

import com.sportflow.features.competitions.domain.model.TournamentBracket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TournamentBracketRepositoryPort {
    TournamentBracket save(TournamentBracket bracket);
    Optional<TournamentBracket> findById(UUID id);
    Optional<TournamentBracket> findByPartidoId(UUID partidoId);
    List<TournamentBracket> findByPhaseIdOrderByRondaAscOrdenAsc(UUID phaseId);
    void deleteById(UUID id);
}
