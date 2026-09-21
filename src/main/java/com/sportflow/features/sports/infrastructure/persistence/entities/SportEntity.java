package com.sportflow.features.sports.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "deportes")
public class SportEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "nombre_canonico", nullable = false, unique = true, length = 100)
    private String nombreCanonico;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "deporte_padre_id")
    private UUID deportePadreId;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    public SportEntity() {}

    public SportEntity(UUID id, String nombreCanonico, String descripcion, boolean activo,
                       UUID deportePadreId, Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.nombreCanonico = nombreCanonico;
        this.descripcion = descripcion;
        this.activo = activo;
        this.deportePadreId = deportePadreId;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombreCanonico() { return nombreCanonico; }
    public void setNombreCanonico(String nombreCanonico) { this.nombreCanonico = nombreCanonico; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public UUID getDeportePadreId() { return deportePadreId; }
    public void setDeportePadreId(UUID deportePadreId) { this.deportePadreId = deportePadreId; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }

    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
