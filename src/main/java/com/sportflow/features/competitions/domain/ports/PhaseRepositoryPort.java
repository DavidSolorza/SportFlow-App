package com.sportflow.features.competitions.domain.ports;

import com.sportflow.features.competitions.domain.model.Phase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhaseRepositoryPort {
    Phase save(Phase phase);
    Optional<Phase> findById(UUID id);
    List<Phase> findByTournamentIdOrderByOrdenAsc(UUID tournamentId);
    List<Phase> findByFasePadreId(UUID fasePadreId);
    boolean existsByTournamentId(UUID tournamentId);
    void deleteById(UUID id);
}
