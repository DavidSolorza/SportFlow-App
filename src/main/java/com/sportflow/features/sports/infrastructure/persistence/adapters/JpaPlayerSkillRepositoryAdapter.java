package com.sportflow.features.sports.infrastructure.persistence.adapters;

import com.sportflow.features.sports.domain.model.PlayerSkill;
import com.sportflow.features.sports.domain.ports.PlayerSkillRepositoryPort;
import com.sportflow.features.sports.infrastructure.persistence.entities.PlayerSkillEntity;
import com.sportflow.features.sports.infrastructure.persistence.entities.PlayerSkillId;
import com.sportflow.features.sports.infrastructure.persistence.repositories.SpringDataPlayerSkillRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaPlayerSkillRepositoryAdapter implements PlayerSkillRepositoryPort {

    private final SpringDataPlayerSkillRepository repository;

    public JpaPlayerSkillRepositoryAdapter(SpringDataPlayerSkillRepository repository) {
        this.repository = repository;
    }

    @Override
    public PlayerSkill save(PlayerSkill playerSkill) {
        PlayerSkillId id = new PlayerSkillId(playerSkill.getPlayerId(), playerSkill.getSkillId());
        PlayerSkillEntity entity = new PlayerSkillEntity(
                id,
                playerSkill.getNivel(),
                playerSkill.getObservacion(),
                playerSkill.getFechaRegistro()
        );
        PlayerSkillEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PlayerSkill> findByPlayerIdAndSkillId(UUID playerId, UUID skillId) {
        return repository.findById(new PlayerSkillId(playerId, skillId)).map(this::toDomain);
    }

    @Override
    public List<PlayerSkill> findByPlayerId(UUID playerId) {
        return repository.findByIdPlayerId(playerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PlayerSkill> findBySkillId(UUID skillId) {
        return repository.findByIdSkillId(skillId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsBySkillId(UUID skillId) {
        return repository.existsByIdSkillId(skillId);
    }

    @Override
    public void deleteByPlayerIdAndSkillId(UUID playerId, UUID skillId) {
        repository.deleteById(new PlayerSkillId(playerId, skillId));
    }

    private PlayerSkill toDomain(PlayerSkillEntity entity) {
        return new PlayerSkill(
                entity.getId().getPlayerId(),
                entity.getId().getSkillId(),
                entity.getNivel(),
                entity.getObservacion(),
                entity.getFechaRegistro()
        );
    }
}
