package com.sportflow.features.competitions.infrastructure.persistence.repositories;

import com.sportflow.features.competitions.infrastructure.persistence.entities.MatchResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataMatchResultRepository extends JpaRepository<MatchResultEntity, UUID> {
    Optional<MatchResultEntity> findByMatchId(UUID matchId);
    void deleteByMatchId(UUID matchId);
}
