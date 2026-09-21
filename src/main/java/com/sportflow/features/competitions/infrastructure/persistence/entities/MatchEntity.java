package com.sportflow.features.competitions.infrastructure.persistence.entities;

import com.sportflow.features.competitions.domain.model.MatchStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "partidos")
public class MatchEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "fase_id", nullable = false)
    private UUID phaseId;

    @Column(name = "grupo_id")
    private UUID groupId;

    @Column(name = "equipo_local_id")
    private UUID equipoLocalId;

    @Column(name = "equipo_visitante_id")
    private UUID equipoVisitanteId;

    @Column(name = "fecha_hora_programada", nullable = false)
    private Instant fechaHoraProgramada;

    @Column(name = "escenario", length = 150)
    private String escenario;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private MatchStatus estado;

    @Column(name = "resultado_confirmado", nullable = false)
    private boolean resultadoConfirmado;

    @Column(name = "partido_origen_1")
    private UUID partidoOrigen1;

    @Column(name = "partido_origen_2")
    private UUID partidoOrigen2;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    public MatchEntity() {}

    public MatchEntity(UUID id, UUID phaseId, UUID groupId, UUID equipoLocalId, UUID equipoVisitanteId,
                       Instant fechaHoraProgramada, String escenario, MatchStatus estado,
                       boolean resultadoConfirmado, UUID partidoOrigen1, UUID partidoOrigen2,
                       Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.phaseId = phaseId;
        this.groupId = groupId;
        this.equipoLocalId = equipoLocalId;
        this.equipoVisitanteId = equipoVisitanteId;
        this.fechaHoraProgramada = fechaHoraProgramada;
        this.escenario = escenario;
        this.estado = estado;
        this.resultadoConfirmado = resultadoConfirmado;
        this.partidoOrigen1 = partidoOrigen1;
        this.partidoOrigen2 = partidoOrigen2;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPhaseId() { return phaseId; }
    public void setPhaseId(UUID phaseId) { this.phaseId = phaseId; }

    public UUID getGroupId() { return groupId; }
    public void setGroupId(UUID groupId) { this.groupId = groupId; }

    public UUID getEquipoLocalId() { return equipoLocalId; }
    public void setEquipoLocalId(UUID equipoLocalId) { this.equipoLocalId = equipoLocalId; }

    public UUID getEquipoVisitanteId() { return equipoVisitanteId; }
    public void setEquipoVisitanteId(UUID equipoVisitanteId) { this.equipoVisitanteId = equipoVisitanteId; }

    public Instant getFechaHoraProgramada() { return fechaHoraProgramada; }
    public void setFechaHoraProgramada(Instant fechaHoraProgramada) { this.fechaHoraProgramada = fechaHoraProgramada; }

    public String getEscenario() { return escenario; }
    public void setEscenario(String escenario) { this.escenario = escenario; }

    public MatchStatus getEstado() { return estado; }
    public void setEstado(MatchStatus estado) { this.estado = estado; }

    public boolean isResultadoConfirmado() { return resultadoConfirmado; }
    public void setResultadoConfirmado(boolean resultadoConfirmado) { this.resultadoConfirmado = resultadoConfirmado; }

    public UUID getPartidoOrigen1() { return partidoOrigen1; }
    public void setPartidoOrigen1(UUID partidoOrigen1) { this.partidoOrigen1 = partidoOrigen1; }

    public UUID getPartidoOrigen2() { return partidoOrigen2; }
    public void setPartidoOrigen2(UUID partidoOrigen2) { this.partidoOrigen2 = partidoOrigen2; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }

    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
