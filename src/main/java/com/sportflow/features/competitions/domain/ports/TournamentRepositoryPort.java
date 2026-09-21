package com.sportflow.features.competitions.domain.ports;

import com.sportflow.features.competitions.domain.model.Tournament;
import com.sportflow.features.competitions.domain.model.TournamentStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TournamentRepositoryPort {
    Tournament save(Tournament tournament);
    Optional<Tournament> findById(UUID id);
    List<Tournament> findAll();
    List<Tournament> findBySportId(UUID sportId);
    List<Tournament> findByEstado(TournamentStatus estado);
    void deleteById(UUID id);
}
