package com.sportflow.features.competitions.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CompetitionGroup {
    private final UUID id;
    private final UUID phaseId;
    private String nombre;
    private int orden;
    private final Set<UUID> equipoIds;
    private final Instant creadoEn;

    public CompetitionGroup(UUID id, UUID phaseId, String nombre, int orden,
                            Set<UUID> equipoIds, Instant creadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.phaseId = validarNoNulo(phaseId, "phaseId");
        this.nombre = validarNoVacio(nombre, "nombre");
        this.orden = orden;
        this.equipoIds = equipoIds != null ? new HashSet<>(equipoIds) : new HashSet<>();
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static CompetitionGroup crear(UUID phaseId, String nombre, int orden) {
        return new CompetitionGroup(UUID.randomUUID(), phaseId, nombre, orden, new HashSet<>(), Instant.now());
    }

    public void agregarEquipo(UUID equipoId) {
        if (equipoId == null) {
            throw new BusinessRuleException("El identificador del equipo no puede ser nulo.");
        }
        if (this.equipoIds.contains(equipoId)) {
            throw new BusinessRuleException("El equipo ya se encuentra asignado a este grupo.");
        }
        this.equipoIds.add(equipoId);
    }

    public void removerEquipo(UUID equipoId) {
        this.equipoIds.remove(equipoId);
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
    public int getOrden() { return orden; }
    public Set<UUID> getEquipoIds() { return Collections.unmodifiableSet(equipoIds); }
    public Instant getCreadoEn() { return creadoEn; }
}
