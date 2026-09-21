package com.sportflow.features.sports.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "habilidades")
public class SkillEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "nombre_canonico", nullable = false, unique = true, length = 100)
    private String nombreCanonico;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public SkillEntity() {}

    public SkillEntity(UUID id, String nombreCanonico, String descripcion, boolean activo, Instant creadoEn) {
        this.id = id;
        this.nombreCanonico = nombreCanonico;
        this.descripcion = descripcion;
        this.activo = activo;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombreCanonico() { return nombreCanonico; }
    public void setNombreCanonico(String nombreCanonico) { this.nombreCanonico = nombreCanonico; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
