package com.sportflow.features.competitions.infrastructure.persistence.repositories;

import com.sportflow.features.competitions.infrastructure.persistence.entities.TournamentBracketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTournamentBracketRepository extends JpaRepository<TournamentBracketEntity, UUID> {
    Optional<TournamentBracketEntity> findByPartidoId(UUID partidoId);
    List<TournamentBracketEntity> findByPhaseIdOrderByRondaAscOrdenAsc(UUID phaseId);
}
