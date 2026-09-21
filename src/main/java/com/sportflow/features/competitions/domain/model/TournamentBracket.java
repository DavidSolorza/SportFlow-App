package com.sportflow.features.competitions.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.UUID;

public class TournamentBracket {
    private final UUID id;
    private final UUID phaseId;
    private String nombre;
    private int ronda;
    private int orden;
    private UUID partidoId;
    private UUID ganadorEquipoId;
    private final Instant creadoEn;

    public TournamentBracket(UUID id, UUID phaseId, String nombre, int ronda, int orden,
                             UUID partidoId, UUID ganadorEquipoId, Instant creadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.phaseId = validarNoNulo(phaseId, "phaseId");
        this.nombre = validarNoVacio(nombre, "nombre");
        this.ronda = ronda;
        this.orden = orden;
        this.partidoId = partidoId;
        this.ganadorEquipoId = ganadorEquipoId;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static TournamentBracket crear(UUID phaseId, String nombre, int ronda, int orden, UUID partidoId) {
        return new TournamentBracket(UUID.randomUUID(), phaseId, nombre, ronda, orden, partidoId, null, Instant.now());
    }

    public void registrarGanador(UUID equipoId) {
        this.ganadorEquipoId = equipoId;
    }

    private String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BusinessRuleException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }

    private <T> T validarNoNulo(T valor, String campo) {
        if (valor == null) {
            throw new BusinessRuleException("El campo " + campo + " es obligatorio.");
        }
        return valor;
    }

    public UUID getId() { return id; }
    public UUID getPhaseId() { return phaseId; }
    public String getNombre() { return nombre; }
    public int getRonda() { return ronda; }
    public int getOrden() { return orden; }
    public UUID getPartidoId() { return partidoId; }
    public UUID getGanadorEquipoId() { return ganadorEquipoId; }
    public Instant getCreadoEn() { return creadoEn; }
}
