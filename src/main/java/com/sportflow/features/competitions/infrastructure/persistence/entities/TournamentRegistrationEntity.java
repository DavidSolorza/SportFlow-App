package com.sportflow.features.competitions.infrastructure.persistence.entities;

import com.sportflow.features.competitions.domain.model.RegistrationStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "inscripciones_torneos")
public class TournamentRegistrationEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "torneo_id", nullable = false)
    private UUID tournamentId;

    @Column(name = "equipo_id", nullable = false)
    private UUID teamId;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private RegistrationStatus estado;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public TournamentRegistrationEntity() {}

    public TournamentRegistrationEntity(UUID id, UUID tournamentId, UUID teamId,
                                        LocalDate fechaInscripcion, RegistrationStatus estado,
                                        String observaciones, Instant creadoEn) {
        this.id = id;
        this.tournamentId = tournamentId;
        this.teamId = teamId;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
        this.observaciones = observaciones;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTournamentId() { return tournamentId; }
    public void setTournamentId(UUID tournamentId) { this.tournamentId = tournamentId; }

    public UUID getTeamId() { return teamId; }
    public void setTeamId(UUID teamId) { this.teamId = teamId; }

    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public RegistrationStatus getEstado() { return estado; }
    public void setEstado(RegistrationStatus estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
