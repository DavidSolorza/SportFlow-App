package com.sportflow.features.sports.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class PlayerSkillId implements Serializable {

    @Column(name = "jugador_id", nullable = false)
    private UUID playerId;

    @Column(name = "habilidad_id", nullable = false)
    private UUID skillId;

    public PlayerSkillId() {}

    public PlayerSkillId(UUID playerId, UUID skillId) {
        this.playerId = playerId;
        this.skillId = skillId;
    }

    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }

    public UUID getSkillId() { return skillId; }
    public void setSkillId(UUID skillId) { this.skillId = skillId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerSkillId that)) return false;
        return Objects.equals(playerId, that.playerId) && Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, skillId);
    }
}
