package com.sportflow.features.competitions.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.UUID;

public class MatchEvent {
    private final UUID id;
    private final UUID matchId;
    private final UUID jugadorId;
    private final UUID equipoId;
    private final EventType tipoEvento;
    private final int minuto;
    private final String descripcion;
    private final String estadoValidacion;
    private final Instant creadoEn;

    public MatchEvent(UUID id, UUID matchId, UUID jugadorId, UUID equipoId, EventType tipoEvento,
                      int minuto, String descripcion, String estadoValidacion, Instant creadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.matchId = validarNoNulo(matchId, "matchId");
        this.jugadorId = jugadorId;
        this.equipoId = validarNoNulo(equipoId, "equipoId");
        this.tipoEvento = validarNoNulo(tipoEvento, "tipoEvento");
        this.minuto = validarMinuto(minuto);
        this.descripcion = descripcion;
        this.estadoValidacion = estadoValidacion != null ? estadoValidacion : "VALIDADO";
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static MatchEvent crear(UUID matchId, UUID jugadorId, UUID equipoId, EventType tipoEvento,
                                   int minuto, String descripcion) {
        return new MatchEvent(UUID.randomUUID(), matchId, jugadorId, equipoId, tipoEvento,
                minuto, descripcion, "VALIDADO", Instant.now());
    }

    private int validarMinuto(int minuto) {
        if (minuto < 0 || minuto > 150) {
            throw new BusinessRuleException("El minuto del evento debe estar entre 0 y 150.");
        }
        return minuto;
    }

    private <T> T validarNoNulo(T valor, String campo) {
        if (valor == null) {
            throw new BusinessRuleException("El campo " + campo + " es obligatorio.");
        }
        return valor;
    }

    public UUID getId() { return id; }
    public UUID getMatchId() { return matchId; }
    public UUID getJugadorId() { return jugadorId; }
    public UUID getEquipoId() { return equipoId; }
    public EventType getTipoEvento() { return tipoEvento; }
    public int getMinuto() { return minuto; }
    public String getDescripcion() { return descripcion; }
    public String getEstadoValidacion() { return estadoValidacion; }
    public Instant getCreadoEn() { return creadoEn; }
}
