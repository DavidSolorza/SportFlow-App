package com.sportflow.features.competitions.infrastructure.persistence.repositories;

import com.sportflow.features.competitions.domain.model.TournamentStatus;
import com.sportflow.features.competitions.infrastructure.persistence.entities.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataTournamentRepository extends JpaRepository<TournamentEntity, UUID> {
    List<TournamentEntity> findBySportId(UUID sportId);
    List<TournamentEntity> findByEstado(TournamentStatus estado);
}
