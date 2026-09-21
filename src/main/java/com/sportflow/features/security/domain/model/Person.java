package com.sportflow.features.security.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.UUID;

public class Person {
    private UUID id;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private PersonStatus estado;
    private Instant creadoEn;
    private Instant actualizadoEn;

    public Person(UUID id, String tipoDocumento, String numeroDocumento, String nombres, String apellidos,
                  String email, String telefono, PersonStatus estado, Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.tipoDocumento = validarNoVacio(tipoDocumento, "tipoDocumento");
        this.numeroDocumento = validarNoVacio(numeroDocumento, "numeroDocumento");
        this.nombres = validarNoVacio(nombres, "nombres");
        this.apellidos = validarNoVacio(apellidos, "apellidos");
        this.email = validarNoVacio(email, "email");
        this.telefono = telefono;
        this.estado = estado != null ? estado : PersonStatus.ACTIVO;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public static Person crear(String tipoDocumento, String numeroDocumento, String nombres, String apellidos,
                               String email, String telefono) {
        return new Person(UUID.randomUUID(), tipoDocumento, numeroDocumento, nombres, apellidos,
                email, telefono, PersonStatus.ACTIVO, Instant.now(), Instant.now());
    }

    public void actualizarDatos(String nombres, String apellidos, String telefono) {
        this.nombres = validarNoVacio(nombres, "nombres");
        this.apellidos = validarNoVacio(apellidos, "apellidos");
        this.telefono = telefono;
        this.actualizadoEn = Instant.now();
    }

    public void suspender() {
        this.estado = PersonStatus.INACTIVO;
        this.actualizadoEn = Instant.now();
    }

    public void bloquear() {
        this.estado = PersonStatus.BLOQUEADO;
        this.actualizadoEn = Instant.now();
    }

    public void activar() {
        this.estado = PersonStatus.ACTIVO;
        this.actualizadoEn = Instant.now();
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    private String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BusinessRuleException("INVALID_PERSON_FIELD", "El campo " + campo + " no puede ser nulo o vacío.");
        }
        return valor.trim();
    }

    // Getters
    public UUID getId() { return id; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public PersonStatus getEstado() { return estado; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
}
