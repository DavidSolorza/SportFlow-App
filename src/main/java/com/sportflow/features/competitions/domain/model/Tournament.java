package com.sportflow.features.competitions.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Tournament {
    private final UUID id;
    private final UUID sportId;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaCierreInscripcion;
    private int cupoEquipos;
    private TournamentStatus estado;
    private final Instant creadoEn;
    private Instant actualizadoEn;

    public Tournament(UUID id, UUID sportId, String nombre, String descripcion,
                      LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaCierreInscripcion,
                      int cupoEquipos, TournamentStatus estado, Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.sportId = validarNoNulo(sportId, "sportId");
        this.nombre = validarNoVacio(nombre, "nombre");
        this.descripcion = descripcion;
        this.fechaInicio = validarNoNulo(fechaInicio, "fechaInicio");
        this.fechaFin = validarNoNulo(fechaFin, "fechaFin");
        this.fechaCierreInscripcion = validarNoNulo(fechaCierreInscripcion, "fechaCierreInscripcion");
        this.cupoEquipos = validarCupo(cupoEquipos);
        this.estado = estado != null ? estado : TournamentStatus.REGISTRO_ABIERTO;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();

        validarConsistenciaFechas();
    }

    public static Tournament crear(UUID sportId, String nombre, String descripcion,
                                   LocalDate fechaInicio, LocalDate fechaFin,
                                   LocalDate fechaCierreInscripcion, int cupoEquipos) {
        return new Tournament(UUID.randomUUID(), sportId, nombre, descripcion, fechaInicio,
                fechaFin, fechaCierreInscripcion, cupoEquipos, TournamentStatus.REGISTRO_ABIERTO,
                Instant.now(), Instant.now());
    }

    public void actualizar(String nombre, String descripcion, LocalDate fechaInicio,
                           LocalDate fechaFin, LocalDate fechaCierreInscripcion,
                           Integer cupoEquipos, TournamentStatus estado) {
        this.nombre = validarNoVacio(nombre, "nombre");
        this.descripcion = descripcion;
        if (fechaInicio != null) this.fechaInicio = fechaInicio;
        if (fechaFin != null) this.fechaFin = fechaFin;
        if (fechaCierreInscripcion != null) this.fechaCierreInscripcion = fechaCierreInscripcion;
        if (cupoEquipos != null) this.cupoEquipos = validarCupo(cupoEquipos);
        if (estado != null) this.estado = estado;
        validarConsistenciaFechas();
        this.actualizadoEn = Instant.now();
    }

    public void cerrarInscripciones() {
        if (this.estado != TournamentStatus.REGISTRO_ABIERTO) {
            throw new BusinessRuleException("Solo se pueden cerrar inscripciones de torneos en estado REGISTRO_ABIERTO.");
        }
        this.estado = TournamentStatus.REGISTRO_CERRADO;
        this.actualizadoEn = Instant.now();
    }

    public void comenzarTorneo() {
        this.estado = TournamentStatus.EN_CURSO;
        this.actualizadoEn = Instant.now();
    }

    public void finalizarTorneo() {
        this.estado = TournamentStatus.FINALIZADO;
        this.actualizadoEn = Instant.now();
    }

    public void cancelar() {
        this.estado = TournamentStatus.CANCELADO;
        this.actualizadoEn = Instant.now();
    }

    public boolean estaEnPeriodoDeInscripcion() {
        if (this.estado != TournamentStatus.REGISTRO_ABIERTO) {
            return false;
        }
        return !LocalDate.now().isAfter(fechaCierreInscripcion);
    }

    private void validarConsistenciaFechas() {
        if (fechaFin.isBefore(fechaInicio)) {
            throw new BusinessRuleException("La fecha de fin del torneo no puede ser anterior a la fecha de inicio.");
        }
        if (fechaCierreInscripcion.isAfter(fechaInicio)) {
            throw new BusinessRuleException("La fecha de cierre de inscripciones debe ser anterior o igual al inicio del torneo.");
        }
    }

    private int validarCupo(int cupo) {
        if (cupo < 2) {
            throw new BusinessRuleException("El cupo de equipos debe ser de al menos 2 participantes.");
        }
        return cupo;
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
    public UUID getSportId() { return sportId; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public LocalDate getFechaCierreInscripcion() { return fechaCierreInscripcion; }
    public int getCupoEquipos() { return cupoEquipos; }
    public TournamentStatus getEstado() { return estado; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
}
