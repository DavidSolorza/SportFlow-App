package com.sportflow.features.competitions.infrastructure.persistence.entities;

import com.sportflow.features.competitions.domain.model.PhaseStatus;
import com.sportflow.features.competitions.domain.model.PhaseType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fases")
public class PhaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "torneo_id", nullable = false)
    private UUID tournamentId;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 30)
    private PhaseType tipo;

    @Column(name = "orden", nullable = false)
    private int orden;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private PhaseStatus estado;

    @Column(name = "fase_padre_id")
    private UUID fasePadreId;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    public PhaseEntity() {}

    public PhaseEntity(UUID id, UUID tournamentId, String nombre, PhaseType tipo, int orden,
                       PhaseStatus estado, UUID fasePadreId, Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.nombre = nombre;
        this.tipo = tipo;
        this.orden = orden;
        this.estado = estado;
        this.fasePadreId = fasePadreId;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTournamentId() { return tournamentId; }
    public void setTournamentId(UUID tournamentId) { this.tournamentId = tournamentId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public PhaseType getTipo() { return tipo; }
    public void setTipo(PhaseType tipo) { this.tipo = tipo; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public PhaseStatus getEstado() { return estado; }
    public void setEstado(PhaseStatus estado) { this.estado = estado; }

    public UUID getFasePadreId() { return fasePadreId; }
    public void setFasePadreId(UUID fasePadreId) { this.fasePadreId = fasePadreId; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }

    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
