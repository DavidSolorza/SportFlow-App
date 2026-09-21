package com.sportflow.features.sports.infrastructure.persistence.entities;

import com.sportflow.features.sports.domain.model.TeamGender;
import com.sportflow.features.sports.domain.model.TeamStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "equipos")
public class TeamEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "club_id")
    private UUID clubId;

    @Column(name = "nombre_distintivo", nullable = false, length = 150)
    private String nombreDistintivo;

    @Column(name = "ciudad", nullable = false, length = 100)
    private String ciudad;

    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero", nullable = false, length = 20)
    private TeamGender genero;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private TeamStatus estado;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "equipo_deportes",
            joinColumns = @JoinColumn(name = "equipo_id")
    )
    @Column(name = "deporte_id")
    private Set<UUID> deporteIds = new HashSet<>();

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    public TeamEntity() {}

    public TeamEntity(UUID id, UUID clubId, String nombreDistintivo, String ciudad, String categoria,
                      TeamGender genero, TeamStatus estado, LocalDate fechaInscripcion,
                      Set<UUID> deporteIds, Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.clubId = clubId;
        this.nombreDistintivo = nombreDistintivo;
        this.ciudad = ciudad;
        this.categoria = categoria;
        this.genero = genero;
        this.estado = estado;
        this.fechaInscripcion = fechaInscripcion;
        this.deporteIds = deporteIds != null ? deporteIds : new HashSet<>();
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getClubId() { return clubId; }
    public void setClubId(UUID clubId) { this.clubId = clubId; }

    public String getNombreDistintivo() { return nombreDistintivo; }
    public void setNombreDistintivo(String nombreDistintivo) { this.nombreDistintivo = nombreDistintivo; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public TeamGender getGenero() { return genero; }
    public void setGenero(TeamGender genero) { this.genero = genero; }

    public TeamStatus getEstado() { return estado; }
    public void setEstado(TeamStatus estado) { this.estado = estado; }

    public LocalDate getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDate fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public Set<UUID> getDeporteIds() { return deporteIds; }
    public void setDeporteIds(Set<UUID> deporteIds) { this.deporteIds = deporteIds; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }

    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
