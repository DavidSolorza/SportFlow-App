package com.sportflow.features.competitions.domain.ports;

import com.sportflow.features.competitions.domain.model.StandingsEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StandingsRepositoryPort {
    StandingsEntry save(StandingsEntry entry);
    List<StandingsEntry> saveAll(List<StandingsEntry> entries);
    Optional<StandingsEntry> findByPhaseIdAndGroupIdAndTeamId(UUID phaseId, UUID groupId, UUID teamId);
    List<StandingsEntry> findByPhaseIdAndGroupIdOrderByPuntosDescDiferenciaGolesDescGolesFavorDesc(UUID phaseId, UUID groupId);
    List<StandingsEntry> findByPhaseIdOrderByPuntosDescDiferenciaGolesDescGolesFavorDesc(UUID phaseId);
    void deleteByPhaseId(UUID phaseId);
}
