package com.sportflow.features.sports.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Player {
    private final UUID id;
    private UUID personaId;
    private String tipoDocumento;
    private String numeroIdentificacion;
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private String posicion;
    private PlayerStatus estado;
    private final Instant creadoEn;
    private Instant actualizadoEn;

    public Player(UUID id, UUID personaId, String tipoDocumento, String numeroIdentificacion,
                  String nombres, String apellidos, LocalDate fechaNacimiento, String posicion,
                  PlayerStatus estado, Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.personaId = personaId;
        this.tipoDocumento = validarNoVacio(tipoDocumento, "tipoDocumento");
        this.numeroIdentificacion = validarNoVacio(numeroIdentificacion, "numeroIdentificacion");
        this.nombres = validarNoVacio(nombres, "nombres");
        this.apellidos = validarNoVacio(apellidos, "apellidos");
        this.fechaNacimiento = validarFechaNacimiento(fechaNacimiento);
        this.posicion = posicion;
        this.estado = estado != null ? estado : PlayerStatus.ACTIVO;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public static Player crear(UUID personaId, String tipoDocumento, String numeroIdentificacion,
                               String nombres, String apellidos, LocalDate fechaNacimiento, String posicion) {
        return new Player(UUID.randomUUID(), personaId, tipoDocumento, numeroIdentificacion, nombres,
                apellidos, fechaNacimiento, posicion, PlayerStatus.ACTIVO, Instant.now(), Instant.now());
    }

    public void actualizar(String nombres, String apellidos, LocalDate fechaNacimiento, String posicion, PlayerStatus estado) {
        this.nombres = validarNoVacio(nombres, "nombres");
        this.apellidos = validarNoVacio(apellidos, "apellidos");
        this.fechaNacimiento = validarFechaNacimiento(fechaNacimiento);
        this.posicion = posicion;
        if (estado != null) {
            this.estado = estado;
        }
        this.actualizadoEn = Instant.now();
    }

    public void suspender() {
        this.estado = PlayerStatus.INACTIVO;
        this.actualizadoEn = Instant.now();
    }

    public void activar() {
        this.estado = PlayerStatus.ACTIVO;
        this.actualizadoEn = Instant.now();
    }

    private String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BusinessRuleException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }

    private LocalDate validarFechaNacimiento(LocalDate fecha) {
        if (fecha == null) {
            throw new BusinessRuleException("La fecha de nacimiento es obligatoria.");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new BusinessRuleException("La fecha de nacimiento no puede ser en el futuro.");
        }
        return fecha;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPersonaId() {
        return personaId;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getPosicion() {
        return posicion;
    }

    public PlayerStatus getEstado() {
        return estado;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }
}
