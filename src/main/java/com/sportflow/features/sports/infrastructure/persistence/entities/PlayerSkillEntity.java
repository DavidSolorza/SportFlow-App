package com.sportflow.features.sports.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "jugador_habilidades")
public class PlayerSkillEntity {

    @EmbeddedId
    private PlayerSkillId id;

    @Column(name = "nivel", nullable = false, length = 30)
    private String nivel;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    public PlayerSkillEntity() {}

    public PlayerSkillEntity(PlayerSkillId id, String nivel, String observacion, LocalDate fechaRegistro) {
        this.id = id;
        this.nivel = nivel;
        this.observacion = observacion;
        this.fechaRegistro = fechaRegistro;
    }

    public PlayerSkillId getId() { return id; }
    public void setId(PlayerSkillId id) { this.id = id; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
