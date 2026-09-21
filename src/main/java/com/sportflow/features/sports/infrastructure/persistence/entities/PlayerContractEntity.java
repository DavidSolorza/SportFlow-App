package com.sportflow.features.sports.infrastructure.persistence.entities;

import com.sportflow.features.sports.domain.model.ContractStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "contratos_jugadores")
public class PlayerContractEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "jugador_id", nullable = false)
    private UUID playerId;

    @Column(name = "equipo_id", nullable = false)
    private UUID teamId;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "numero_camiseta")
    private Integer numeroCamiseta;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private ContractStatus estado;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    public PlayerContractEntity() {}

    public PlayerContractEntity(UUID id, UUID playerId, UUID teamId, LocalDate fechaInicio,
                                LocalDate fechaFin, Integer numeroCamiseta, ContractStatus estado,
                                String observaciones, Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.playerId = playerId;
        this.teamId = teamId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numeroCamiseta = numeroCamiseta;
        this.estado = estado;
        this.observaciones = observaciones;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPlayerId() { return playerId; }
    public void setPlayerId(UUID playerId) { this.playerId = playerId; }

    public UUID getTeamId() { return teamId; }
    public void setTeamId(UUID teamId) { this.teamId = teamId; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getNumeroCamiseta() { return numeroCamiseta; }
    public void setNumeroCamiseta(Integer numeroCamiseta) { this.numeroCamiseta = numeroCamiseta; }

    public ContractStatus getEstado() { return estado; }
    public void setEstado(ContractStatus estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }

    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
