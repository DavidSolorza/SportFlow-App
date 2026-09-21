package com.sportflow.features.competitions.infrastructure.persistence.repositories;

import com.sportflow.features.competitions.domain.model.RegistrationStatus;
import com.sportflow.features.competitions.infrastructure.persistence.entities.TournamentRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTournamentRegistrationRepository extends JpaRepository<TournamentRegistrationEntity, UUID> {
    Optional<TournamentRegistrationEntity> findByTournamentIdAndTeamId(UUID tournamentId, UUID teamId);
    List<TournamentRegistrationEntity> findByTournamentId(UUID tournamentId);
    int countByTournamentIdAndEstado(UUID tournamentId, RegistrationStatus estado);
    boolean existsByTournamentIdAndTeamId(UUID tournamentId, UUID teamId);
}
