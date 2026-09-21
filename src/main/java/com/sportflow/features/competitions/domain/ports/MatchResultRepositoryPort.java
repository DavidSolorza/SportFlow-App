package com.sportflow.features.competitions.domain.ports;

import com.sportflow.features.competitions.domain.model.MatchResult;

import java.util.Optional;
import java.util.UUID;

public interface MatchResultRepositoryPort {
    MatchResult save(MatchResult result);
    Optional<MatchResult> findById(UUID id);
    Optional<MatchResult> findByMatchId(UUID matchId);
    void deleteByMatchId(UUID matchId);
}
