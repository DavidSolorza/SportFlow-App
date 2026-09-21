package com.sportflow.features.competitions.domain.ports;

import com.sportflow.features.competitions.domain.model.CompetitionGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompetitionGroupRepositoryPort {
    CompetitionGroup save(CompetitionGroup group);
    Optional<CompetitionGroup> findById(UUID id);
    List<CompetitionGroup> findByPhaseIdOrderByOrdenAsc(UUID phaseId);
    boolean existsByPhaseIdAndEquipoId(UUID phaseId, UUID equipoId);
    void deleteById(UUID id);
}
