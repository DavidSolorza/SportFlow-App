package com.sportflow.features.competitions.infrastructure.persistence.repositories;

import com.sportflow.features.competitions.infrastructure.persistence.entities.PhaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataPhaseRepository extends JpaRepository<PhaseEntity, UUID> {
    List<PhaseEntity> findByTournamentIdOrderByOrdenAsc(UUID tournamentId);
    List<PhaseEntity> findByFasePadreId(UUID fasePadreId);
    boolean existsByTournamentId(UUID tournamentId);
}
