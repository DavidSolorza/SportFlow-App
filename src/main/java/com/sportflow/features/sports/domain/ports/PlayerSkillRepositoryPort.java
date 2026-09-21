package com.sportflow.features.sports.domain.ports;

import com.sportflow.features.sports.domain.model.PlayerSkill;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerSkillRepositoryPort {
    PlayerSkill save(PlayerSkill playerSkill);
    Optional<PlayerSkill> findByPlayerIdAndSkillId(UUID playerId, UUID skillId);
    List<PlayerSkill> findByPlayerId(UUID playerId);
    List<PlayerSkill> findBySkillId(UUID skillId);
    boolean existsBySkillId(UUID skillId);
    void deleteByPlayerIdAndSkillId(UUID playerId, UUID skillId);
}
