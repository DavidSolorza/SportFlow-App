package com.sportflow.features.sports.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team {
    private final UUID id;
    private UUID clubId;
    private String nombreDistintivo;
    private String ciudad;
    private String categoria;
    private TeamGender genero;
    private TeamStatus estado;
    private final LocalDate fechaInscripcion;
    private final Set<UUID> deporteIds;
    private final Instant creadoEn;
    private Instant actualizadoEn;

    public Team(UUID id, UUID clubId, String nombreDistintivo, String ciudad, String categoria,
                TeamGender genero, TeamStatus estado, LocalDate fechaInscripcion, Set<UUID> deporteIds,
                Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.clubId = clubId;
        this.nombreDistintivo = validarNoVacio(nombreDistintivo, "nombreDistintivo");
        this.ciudad = validarNoVacio(ciudad, "ciudad");
        this.categoria = validarNoVacio(categoria, "categoria");
        this.genero = genero != null ? genero : TeamGender.MIXTO;
        this.estado = estado != null ? estado : TeamStatus.ACTIVO;
        this.fechaInscripcion = fechaInscripcion != null ? fechaInscripcion : LocalDate.now();
        this.deporteIds = deporteIds != null ? new HashSet<>(deporteIds) : new HashSet<>();
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public static Team crear(UUID clubId, String nombreDistintivo, String ciudad, String categoria, TeamGender genero) {
        return new Team(UUID.randomUUID(), clubId, nombreDistintivo, ciudad, categoria, genero,
                TeamStatus.ACTIVO, LocalDate.now(), new HashSet<>(), Instant.now(), Instant.now());
    }

    public void actualizar(String nombreDistintivo, String ciudad, String categoria, TeamGender genero, TeamStatus estado, UUID clubId) {
        this.nombreDistintivo = validarNoVacio(nombreDistintivo, "nombreDistintivo");
        this.ciudad = validarNoVacio(ciudad, "ciudad");
        this.categoria = validarNoVacio(categoria, "categoria");
        if (genero != null) {
            this.genero = genero;
        }
        if (estado != null) {
            this.estado = estado;
        }
        this.clubId = clubId;
        this.actualizadoEn = Instant.now();
    }

    public void asociarDeporte(UUID deporteId) {
        if (deporteId == null) {
            throw new BusinessRuleException("El identificador del deporte no puede ser nulo.");
        }
        if (this.deporteIds.contains(deporteId)) {
            throw new BusinessRuleException("El equipo ya se encuentra asociado a este deporte.");
        }
        this.deporteIds.add(deporteId);
        this.actualizadoEn = Instant.now();
    }

    public void desasociarDeporte(UUID deporteId) {
        if (deporteId == null || !this.deporteIds.contains(deporteId)) {
            throw new BusinessRuleException("El equipo no está asociado a este deporte.");
        }
        this.deporteIds.remove(deporteId);
        this.actualizadoEn = Instant.now();
    }

    public boolean practicaDeporte(UUID deporteId) {
        return this.deporteIds.contains(deporteId);
    }

    private String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BusinessRuleException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }

    public UUID getId() {
        return id;
    }

    public UUID getClubId() {
        return clubId;
    }

    public String getNombreDistintivo() {
        return nombreDistintivo;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getCategoria() {
        return categoria;
    }

    public TeamGender getGenero() {
        return genero;
    }

    public TeamStatus getEstado() {
        return estado;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public Set<UUID> getDeporteIds() {
        return Collections.unmodifiableSet(deporteIds);
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }
}
