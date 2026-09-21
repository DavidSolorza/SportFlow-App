package com.sportflow.features.sports.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.LocalDate;
import java.util.UUID;

public class PlayerSkill {
    private final UUID playerId;
    private final UUID skillId;
    private String nivel;
    private String observacion;
    private final LocalDate fechaRegistro;

    public PlayerSkill(UUID playerId, UUID skillId, String nivel, String observacion, LocalDate fechaRegistro) {
        if (playerId == null || skillId == null) {
            throw new BusinessRuleException("El jugador y la habilidad son obligatorios.");
        }
        this.playerId = playerId;
        this.skillId = skillId;
        this.nivel = nivel != null && !nivel.trim().isEmpty() ? nivel.trim() : "INTERMEDIO";
        this.observacion = observacion;
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDate.now();
    }

    public static PlayerSkill crear(UUID playerId, UUID skillId, String nivel, String observacion) {
        return new PlayerSkill(playerId, skillId, nivel, observacion, LocalDate.now());
    }

    public void actualizar(String nivel, String observacion) {
        if (nivel != null && !nivel.trim().isEmpty()) {
            this.nivel = nivel.trim();
        }
        this.observacion = observacion;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getSkillId() {
        return skillId;
    }

    public String getNivel() {
        return nivel;
    }

    public String getObservacion() {
        return observacion;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }
}
