package com.sportflow.features.competitions.infrastructure.persistence.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "grupos")
public class CompetitionGroupEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "fase_id", nullable = false)
    private UUID phaseId;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "orden", nullable = false)
    private int orden;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "equipo_grupos",
            joinColumns = @JoinColumn(name = "grupo_id")
    )
    @Column(name = "equipo_id")
    private Set<UUID> equipoIds = new HashSet<>();

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public CompetitionGroupEntity() {}

    public CompetitionGroupEntity(UUID id, UUID phaseId, String nombre, int orden,
                                  Set<UUID> equipoIds, Instant creadoEn) {
        this.id = id;
        this.phaseId = phaseId;
        this.nombre = nombre;
        this.orden = orden;
        this.equipoIds = equipoIds != null ? equipoIds : new HashSet<>();
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPhaseId() { return phaseId; }
    public void setPhaseId(UUID phaseId) { this.phaseId = phaseId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public Set<UUID> getEquipoIds() { return equipoIds; }
    public void setEquipoIds(Set<UUID> equipoIds) { this.equipoIds = equipoIds; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
