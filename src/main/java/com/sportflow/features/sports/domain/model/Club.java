package com.sportflow.features.sports.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.UUID;

public class Club {
    private final UUID id;
    private String nombre;
    private String ciudad;
    private String datosContacto;
    private boolean activo;
    private final Instant creadoEn;
    private Instant actualizadoEn;

    public Club(UUID id, String nombre, String ciudad, String datosContacto, boolean activo,
                Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.nombre = validarNoVacio(nombre, "nombre");
        this.ciudad = validarNoVacio(ciudad, "ciudad");
        this.datosContacto = datosContacto;
        this.activo = activo;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public static Club crear(String nombre, String ciudad, String datosContacto) {
        return new Club(UUID.randomUUID(), nombre, ciudad, datosContacto, true, Instant.now(), Instant.now());
    }

    public void actualizar(String nombre, String ciudad, String datosContacto, Boolean activo) {
        this.nombre = validarNoVacio(nombre, "nombre");
        this.ciudad = validarNoVacio(ciudad, "ciudad");
        this.datosContacto = datosContacto;
        if (activo != null) {
            this.activo = activo;
        }
        this.actualizadoEn = Instant.now();
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

    public String getNombre() {
        return nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getDatosContacto() {
        return datosContacto;
    }

    public boolean isActivo() {
        return activo;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }
}
