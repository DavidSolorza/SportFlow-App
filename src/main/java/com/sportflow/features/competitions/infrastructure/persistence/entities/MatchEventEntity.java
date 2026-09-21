package com.sportflow.features.competitions.infrastructure.persistence.entities;

import com.sportflow.features.competitions.domain.model.EventType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "eventos_partidos")
public class MatchEventEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "partido_id", nullable = false)
    private UUID matchId;

    @Column(name = "jugador_id")
    private UUID jugadorId;

    @Column(name = "equipo_id", nullable = false)
    private UUID equipoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 30)
    private EventType tipoEvento;

    @Column(name = "minuto", nullable = false)
    private int minuto;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "estado_validacion", nullable = false, length = 20)
    private String estadoValidacion;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public MatchEventEntity() {}

    public MatchEventEntity(UUID id, UUID matchId, UUID jugadorId, UUID equipoId,
                            EventType tipoEvento, int minuto, String descripcion,
                            String estadoValidacion, Instant creadoEn) {
        this.id = id;
        this.matchId = matchId;
        this.jugadorId = jugadorId;
        this.equipoId = equipoId;
        this.tipoEvento = tipoEvento;
        this.minuto = minuto;
        this.descripcion = descripcion;
        this.estadoValidacion = estadoValidacion;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getMatchId() { return matchId; }
    public void setMatchId(UUID matchId) { this.matchId = matchId; }

    public UUID getJugadorId() { return jugadorId; }
    public void setJugadorId(UUID jugadorId) { this.jugadorId = jugadorId; }

    public UUID getEquipoId() { return equipoId; }
    public void setEquipoId(UUID equipoId) { this.equipoId = equipoId; }

    public EventType getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(EventType tipoEvento) { this.tipoEvento = tipoEvento; }

    public int getMinuto() { return minuto; }
    public void setMinuto(int minuto) { this.minuto = minuto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstadoValidacion() { return estadoValidacion; }
    public void setEstadoValidacion(String estadoValidacion) { this.estadoValidacion = estadoValidacion; }

    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
