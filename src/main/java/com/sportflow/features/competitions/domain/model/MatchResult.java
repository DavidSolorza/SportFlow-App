package com.sportflow.features.competitions.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.UUID;

public class MatchResult {
    private final UUID id;
    private final UUID matchId;
    private final int golesLocal;
    private final int golesVisitante;
    private final Instant fechaConfirmacion;
    private final String confirmadoPor;
    private final String observaciones;

    public MatchResult(UUID id, UUID matchId, int golesLocal, int golesVisitante,
                       Instant fechaConfirmacion, String confirmadoPor, String observaciones) {
        this.id = id != null ? id : UUID.randomUUID();
        this.matchId = validarNoNulo(matchId, "matchId");
        this.golesLocal = validarGoles(golesLocal, "golesLocal");
        this.golesVisitante = validarGoles(golesVisitante, "golesVisitante");
        this.fechaConfirmacion = fechaConfirmacion != null ? fechaConfirmacion : Instant.now();
        this.confirmadoPor = confirmadoPor;
        this.observaciones = observaciones;
    }

    public static MatchResult crear(UUID matchId, int golesLocal, int golesVisitante,
                                    String confirmadoPor, String observaciones) {
        return new MatchResult(UUID.randomUUID(), matchId, golesLocal, golesVisitante,
                Instant.now(), confirmadoPor, observaciones);
    }

    private int validarGoles(int goles, String campo) {
        if (goles < 0) {
            throw new BusinessRuleException("El marcador de " + campo + " no puede ser negativo.");
        }
        return goles;
    }

    private <T> T validarNoNulo(T valor, String campo) {
        if (valor == null) {
            throw new BusinessRuleException("El campo " + campo + " es obligatorio.");
        }
        return valor;
    }

    public UUID getId() { return id; }
    public UUID getMatchId() { return matchId; }
    public int getGolesLocal() { return golesLocal; }
    public int getGolesVisitante() { return golesVisitante; }
    public Instant getFechaConfirmacion() { return fechaConfirmacion; }
    public String getConfirmadoPor() { return confirmadoPor; }
    public String getObservaciones() { return observaciones; }
}
