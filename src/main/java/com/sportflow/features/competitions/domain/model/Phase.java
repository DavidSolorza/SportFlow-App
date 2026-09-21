package com.sportflow.features.competitions.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.UUID;

public class Phase {
    private final UUID id;
    private final UUID tournamentId;
    private String nombre;
    private PhaseType tipo;
    private int orden;
    private PhaseStatus estado;
    private UUID fasePadreId;
    private final Instant creadoEn;
    private Instant actualizadoEn;

    public Phase(UUID id, UUID tournamentId, String nombre, PhaseType tipo, int orden,
                 PhaseStatus estado, UUID fasePadreId, Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.tournamentId = validarNoNulo(tournamentId, "tournamentId");
        this.nombre = validarNoVacio(nombre, "nombre");
        this.tipo = tipo != null ? tipo : PhaseType.GRUPOS;
        this.orden = orden;
        this.estado = estado != null ? estado : PhaseStatus.PROGRAMADA;
        this.fasePadreId = fasePadreId;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public static Phase crear(UUID tournamentId, String nombre, PhaseType tipo, int orden, UUID fasePadreId) {
        return new Phase(UUID.randomUUID(), tournamentId, nombre, tipo, orden,
                PhaseStatus.PROGRAMADA, fasePadreId, Instant.now(), Instant.now());
    }

    public void actualizar(String nombre, PhaseType tipo, Integer orden, PhaseStatus estado, UUID fasePadreId) {
        if (fasePadreId != null && fasePadreId.equals(this.id)) {
            throw new BusinessRuleException("Una fase no puede ser subfase de sí misma.");
        }
        this.nombre = validarNoVacio(nombre, "nombre");
        if (tipo != null) this.tipo = tipo;
        if (orden != null) this.orden = orden;
        if (estado != null) this.estado = estado;
        this.fasePadreId = fasePadreId;
        this.actualizadoEn = Instant.now();
    }

    public void iniciarFase() {
        this.estado = PhaseStatus.EN_CURSO;
        this.actualizadoEn = Instant.now();
    }

    public void finalizarFase() {
        this.estado = PhaseStatus.FINALIZADA;
        this.actualizadoEn = Instant.now();
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
    public UUID getTournamentId() { return tournamentId; }
    public String getNombre() { return nombre; }
    public PhaseType getTipo() { return tipo; }
    public int getOrden() { return orden; }
    public PhaseStatus getEstado() { return estado; }
    public UUID getFasePadreId() { return fasePadreId; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
}
