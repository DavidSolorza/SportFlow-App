package com.sportflow.features.sports.infrastructure.persistence.repositories;

import com.sportflow.features.sports.infrastructure.persistence.entities.PlayerSkillEntity;
import com.sportflow.features.sports.infrastructure.persistence.entities.PlayerSkillId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataPlayerSkillRepository extends JpaRepository<PlayerSkillEntity, PlayerSkillId> {
    List<PlayerSkillEntity> findByIdPlayerId(UUID playerId);
    List<PlayerSkillEntity> findByIdSkillId(UUID skillId);
    boolean existsByIdSkillId(UUID skillId);
}
