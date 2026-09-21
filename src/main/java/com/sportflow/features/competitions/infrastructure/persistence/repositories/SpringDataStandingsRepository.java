package com.sportflow.features.competitions.infrastructure.persistence.repositories;

import com.sportflow.features.competitions.infrastructure.persistence.entities.StandingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataStandingsRepository extends JpaRepository<StandingsEntity, UUID> {
    Optional<StandingsEntity> findByPhaseIdAndGroupIdAndTeamId(UUID phaseId, UUID groupId, UUID teamId);
    List<StandingsEntity> findByPhaseIdAndGroupIdOrderByPuntosDescDiferenciaGolesDescGolesFavorDesc(UUID phaseId, UUID groupId);
    List<StandingsEntity> findByPhaseIdOrderByPuntosDescDiferenciaGolesDescGolesFavorDesc(UUID phaseId);
    void deleteByPhaseId(UUID phaseId);
}
