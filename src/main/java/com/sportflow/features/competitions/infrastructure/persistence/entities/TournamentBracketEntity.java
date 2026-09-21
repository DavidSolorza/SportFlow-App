package com.sportflow.features.competitions.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "llaves")
public class TournamentBracketEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "fase_id", nullable = false)
    private UUID phaseId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "ronda", nullable = false)
    private int ronda;

    @Column(name = "orden", nullable = false)
    private int orden;

    @Column(name = "partido_id")
    private UUID partidoId;

    @Column(name = "ganador_equipo_id")
    private UUID ganadorEquipoId;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public TournamentBracketEntity() {}

    public TournamentBracketEntity(UUID id, UUID phaseId, String nombre, int ronda, int orden,
                                   UUID partidoId, UUID ganadorEquipoId, Instant creadoEn) {
        this.id = id;
        this.phaseId = phaseId;
        this.nombre = nombre;
        this.ronda = ronda;
        this.orden = orden;
        this.partidoId = partidoId;
        this.ganadorEquipoId = ganadorEquipoId;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPhaseId() { return phaseId; }
    public void setPhaseId(UUID phaseId) { this.phaseId = phaseId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getRonda() { return ronda; }
    public void setRonda(int ronda) { this.ronda = ronda; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public UUID getPartidoId() { return partidoId; }
    public void setPartidoId(UUID partidoId) { this.partidoId = partidoId; }

    public UUID getGanadorEquipoId() { return ganadorEquipoId; }
    public void setGanadorEquipoId(UUID ganadorEquipoId) { this.ganadorEquipoId = ganadorEquipoId; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
