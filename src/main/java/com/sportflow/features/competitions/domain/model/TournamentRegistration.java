package com.sportflow.features.competitions.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class TournamentRegistration {
    private final UUID id;
    private final UUID tournamentId;
    private final UUID teamId;
    private final LocalDate fechaInscripcion;
    private RegistrationStatus estado;
    private String observaciones;
    private final Instant creadoEn;

    public TournamentRegistration(UUID id, UUID tournamentId, UUID teamId, LocalDate fechaInscripcion,
                                  RegistrationStatus estado, String observaciones, Instant creadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.tournamentId = validarNoNulo(tournamentId, "tournamentId");
        this.teamId = validarNoNulo(teamId, "teamId");
        this.fechaInscripcion = fechaInscripcion != null ? fechaInscripcion : LocalDate.now();
        this.estado = estado != null ? estado : RegistrationStatus.ACEPTADA;
        this.observaciones = observaciones;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static TournamentRegistration crear(UUID tournamentId, UUID teamId, String observaciones) {
        return new TournamentRegistration(UUID.randomUUID(), tournamentId, teamId, LocalDate.now(),
                RegistrationStatus.ACEPTADA, observaciones, Instant.now());
    }

    public void rechazar(String motivo) {
        this.estado = RegistrationStatus.RECHAZADA;
        this.observaciones = motivo;
    }

    public void cancelar() {
        this.estado = RegistrationStatus.CANCELADA;
    }

    private <T> T validarNoNulo(T valor, String campo) {
        if (valor == null) {
            throw new BusinessRuleException("El campo " + campo + " es obligatorio.");
        }
        return valor;
    }

    public UUID getId() { return id; }
    public UUID getTournamentId() { return tournamentId; }
    public UUID getTeamId() { return teamId; }
    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public RegistrationStatus getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public Instant getCreadoEn() { return creadoEn; }
}
