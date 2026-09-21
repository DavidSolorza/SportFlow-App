package com.sportflow.features.competitions.infrastructure.persistence.repositories;

import com.sportflow.features.competitions.infrastructure.persistence.entities.MatchEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataMatchEventRepository extends JpaRepository<MatchEventEntity, UUID> {
    List<MatchEventEntity> findByMatchIdOrderByMinutoAsc(UUID matchId);
}
