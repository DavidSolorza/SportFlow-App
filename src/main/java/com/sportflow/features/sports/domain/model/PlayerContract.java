package com.sportflow.features.sports.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class PlayerContract {
    private final UUID id;
    private final UUID playerId;
    private final UUID teamId;
    private final LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer numeroCamiseta;
    private ContractStatus estado;
    private String observaciones;
    private final Instant creadoEn;
    private Instant actualizadoEn;

    public PlayerContract(UUID id, UUID playerId, UUID teamId, LocalDate fechaInicio,
                          LocalDate fechaFin, Integer numeroCamiseta, ContractStatus estado,
                          String observaciones, Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.playerId = validarNoNulo(playerId, "playerId");
        this.teamId = validarNoNulo(teamId, "teamId");
        this.fechaInicio = validarNoNulo(fechaInicio, "fechaInicio");
        this.fechaFin = fechaFin;
        this.numeroCamiseta = numeroCamiseta;
        this.estado = estado != null ? estado : ContractStatus.ACTIVO;
        this.observaciones = observaciones;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();

        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new BusinessRuleException("La fecha de fin del contrato no puede ser anterior a la fecha de inicio.");
        }
    }

    public static PlayerContract crear(UUID playerId, UUID teamId, LocalDate fechaInicio,
                                       LocalDate fechaFin, Integer numeroCamiseta, String observaciones) {
        return new PlayerContract(UUID.randomUUID(), playerId, teamId, fechaInicio, fechaFin,
                numeroCamiseta, ContractStatus.ACTIVO, observaciones, Instant.now(), Instant.now());
    }

    public void finalizarContrato(LocalDate fechaTerminacion, String motivo) {
        if (this.estado != ContractStatus.ACTIVO) {
            throw new BusinessRuleException("Solo se pueden finalizar contratos que se encuentren en estado ACTIVO.");
        }
        LocalDate fecha = fechaTerminacion != null ? fechaTerminacion : LocalDate.now();
        if (fecha.isBefore(this.fechaInicio)) {
            throw new BusinessRuleException("La fecha de terminación no puede ser anterior al inicio del contrato.");
        }
        this.fechaFin = fecha;
        this.estado = ContractStatus.FINALIZADO;
        if (motivo != null && !motivo.trim().isEmpty()) {
            this.observaciones = (this.observaciones != null ? this.observaciones + " | " : "") + "Finalizado: " + motivo;
        }
        this.actualizadoEn = Instant.now();
    }

    public void rescindirContrato(String motivo) {
        this.finalizarContrato(LocalDate.now(), "Rescindido: " + motivo);
        this.estado = ContractStatus.RESCINDIDO;
    }

    public boolean estaActivo() {
        if (this.estado != ContractStatus.ACTIVO) {
            return false;
        }
        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(fechaInicio)) {
            return false;
        }
        return fechaFin == null || !hoy.isAfter(fechaFin);
    }

    private <T> T validarNoNulo(T valor, String campo) {
        if (valor == null) {
            throw new BusinessRuleException("El campo " + campo + " es obligatorio.");
        }
        return valor;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public Integer getNumeroCamiseta() {
        return numeroCamiseta;
    }

    public ContractStatus getEstado() {
        return estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }
}
