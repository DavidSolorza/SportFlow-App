package com.sportflow.features.competitions.infrastructure.persistence.entities;

import com.sportflow.features.competitions.domain.model.TournamentStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "torneos")
public class TournamentEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "deporte_id", nullable = false)
    private UUID sportId;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "fecha_cierre_inscripcion", nullable = false)
    private LocalDate fechaCierreInscripcion;

    @Column(name = "cupo_equipos", nullable = false)
    private int cupoEquipos;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private TournamentStatus estado;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    public TournamentEntity() {}

    public TournamentEntity(UUID id, UUID sportId, String nombre, String descripcion,
                            LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaCierreInscripcion,
                            int cupoEquipos, TournamentStatus estado, Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.sportId = sportId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaCierreInscripcion = fechaCierreInscripcion;
        this.cupoEquipos = cupoEquipos;
        this.estado = estado;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getSportId() { return sportId; }
    public void setSportId(UUID sportId) { this.sportId = sportId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public LocalDate getFechaCierreInscripcion() { return fechaCierreInscripcion; }
    public void setFechaCierreInscripcion(LocalDate fechaCierreInscripcion) { this.fechaCierreInscripcion = fechaCierreInscripcion; }

    public int getCupoEquipos() { return cupoEquipos; }
    public void setCupoEquipos(int cupoEquipos) { this.cupoEquipos = cupoEquipos; }

    public TournamentStatus getEstado() { return estado; }
    public void setEstado(TournamentStatus estado) { this.estado = estado; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }

    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
