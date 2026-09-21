package com.sportflow.features.competitions.domain.ports;

import com.sportflow.features.competitions.domain.model.MatchEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchEventRepositoryPort {
    MatchEvent save(MatchEvent event);
    Optional<MatchEvent> findById(UUID id);
    List<MatchEvent> findByMatchIdOrderByMinutoAsc(UUID matchId);
    void deleteById(UUID id);
}
