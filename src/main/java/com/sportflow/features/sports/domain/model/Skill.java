package com.sportflow.features.sports.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.UUID;

public class Skill {
    private final UUID id;
    private String nombreCanonico;
    private String descripcion;
    private boolean activo;
    private final Instant creadoEn;

    public Skill(UUID id, String nombreCanonico, String descripcion, boolean activo, Instant creadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.nombreCanonico = validarNoVacio(nombreCanonico, "nombreCanonico");
        this.descripcion = descripcion;
        this.activo = activo;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static Skill crear(String nombreCanonico, String descripcion) {
        return new Skill(UUID.randomUUID(), nombreCanonico, descripcion, true, Instant.now());
    }

    public void actualizar(String nombreCanonico, String descripcion, Boolean activo) {
        this.nombreCanonico = validarNoVacio(nombreCanonico, "nombreCanonico");
        this.descripcion = descripcion;
        if (activo != null) {
            this.activo = activo;
        }
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

    public String getNombreCanonico() {
        return nombreCanonico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }
}
