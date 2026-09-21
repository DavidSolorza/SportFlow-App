package com.sportflow.features.competitions.domain.ports;

import com.sportflow.features.competitions.domain.model.TournamentRegistration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TournamentRegistrationRepositoryPort {
    TournamentRegistration save(TournamentRegistration registration);
    Optional<TournamentRegistration> findById(UUID id);
    Optional<TournamentRegistration> findByTournamentIdAndTeamId(UUID tournamentId, UUID teamId);
    List<TournamentRegistration> findByTournamentId(UUID tournamentId);
    int countAcceptedByTournamentId(UUID tournamentId);
    boolean existsByTournamentIdAndTeamId(UUID tournamentId, UUID teamId);
}
