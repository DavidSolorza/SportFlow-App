package com.sportflow.features.competitions.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.UUID;

public class Match {
    private final UUID id;
    private final UUID phaseId;
    private UUID groupId;
    private UUID equipoLocalId;
    private UUID equipoVisitanteId;
    private Instant fechaHoraProgramada;
    private String escenario;
    private MatchStatus estado;
    private boolean resultadoConfirmado;
    private UUID partidoOrigen1;
    private UUID partidoOrigen2;
    private final Instant creadoEn;
    private Instant actualizadoEn;

    public Match(UUID id, UUID phaseId, UUID groupId, UUID equipoLocalId, UUID equipoVisitanteId,
                 Instant fechaHoraProgramada, String escenario, MatchStatus estado,
                 boolean resultadoConfirmado, UUID partidoOrigen1, UUID partidoOrigen2,
                 Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.phaseId = validarNoNulo(phaseId, "phaseId");
        this.groupId = groupId;
        this.equipoLocalId = equipoLocalId;
        this.equipoVisitanteId = equipoVisitanteId;
        this.fechaHoraProgramada = validarNoNulo(fechaHoraProgramada, "fechaHoraProgramada");
        this.escenario = escenario;
        this.estado = estado != null ? estado : MatchStatus.PROGRAMADO;
        this.resultadoConfirmado = resultadoConfirmado;
        this.partidoOrigen1 = partidoOrigen1;
        this.partidoOrigen2 = partidoOrigen2;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();

        if (equipoLocalId != null && equipoVisitanteId != null && equipoLocalId.equals(equipoVisitanteId)) {
            throw new BusinessRuleException("Un equipo no puede disputar un partido contra sí mismo.");
        }
    }

    public static Match crear(UUID phaseId, UUID groupId, UUID equipoLocalId, UUID equipoVisitanteId,
                              Instant fechaHoraProgramada, String escenario,
                              UUID partidoOrigen1, UUID partidoOrigen2) {
        return new Match(UUID.randomUUID(), phaseId, groupId, equipoLocalId, equipoVisitanteId,
                fechaHoraProgramada, escenario, MatchStatus.PROGRAMADO, false,
                partidoOrigen1, partidoOrigen2, Instant.now(), Instant.now());
    }

    public void asignarEquipos(UUID localId, UUID visitanteId) {
        if (localId != null && visitanteId != null && localId.equals(visitanteId)) {
            throw new BusinessRuleException("Un equipo no puede disputar un partido contra sí mismo.");
        }
        this.equipoLocalId = localId;
        this.equipoVisitanteId = visitanteId;
        this.actualizadoEn = Instant.now();
    }

    public void reprogramar(Instant nuevaFechaHora, String nuevoEscenario) {
        this.fechaHoraProgramada = validarNoNulo(nuevaFechaHora, "nuevaFechaHora");
        this.escenario = nuevoEscenario;
        this.actualizadoEn = Instant.now();
    }

    public void iniciar() {
        this.estado = MatchStatus.EN_CURSO;
        this.actualizadoEn = Instant.now();
    }

    public void confirmarFinalizacion() {
        this.estado = MatchStatus.FINALIZADO;
        this.resultadoConfirmado = true;
        this.actualizadoEn = Instant.now();
    }

    private <T> T validarNoNulo(T valor, String campo) {
        if (valor == null) {
            throw new BusinessRuleException("El campo " + campo + " es obligatorio.");
        }
        return valor;
    }

    public UUID getId() { return id; }
    public UUID getPhaseId() { return phaseId; }
    public UUID getGroupId() { return groupId; }
    public UUID getEquipoLocalId() { return equipoLocalId; }
    public UUID getEquipoVisitanteId() { return equipoVisitanteId; }
    public Instant getFechaHoraProgramada() { return fechaHoraProgramada; }
    public String getEscenario() { return escenario; }
    public MatchStatus getEstado() { return estado; }
    public boolean isResultadoConfirmado() { return resultadoConfirmado; }
    public UUID getPartidoOrigen1() { return partidoOrigen1; }
    public UUID getPartidoOrigen2() { return partidoOrigen2; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
}
