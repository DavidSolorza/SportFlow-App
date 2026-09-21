package com.sportflow.features.competitions.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resultados_partidos")
public class MatchResultEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "partido_id", nullable = false, unique = true)
    private UUID matchId;

    @Column(name = "goles_local", nullable = false)
    private int golesLocal;

    @Column(name = "goles_visitante", nullable = false)
    private int golesVisitante;

    @Column(name = "fecha_confirmacion", nullable = false)
    private Instant fechaConfirmacion;

    @Column(name = "confirmado_por", length = 100)
    private String confirmadoPor;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    public MatchResultEntity() {}

    public MatchResultEntity(UUID id, UUID matchId, int golesLocal, int golesVisitante,
                             Instant fechaConfirmacion, String confirmadoPor, String observaciones) {
        this.id = id;
        this.matchId = matchId;
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.fechaConfirmacion = fechaConfirmacion;
        this.confirmadoPor = confirmadoPor;
        this.observaciones = observaciones;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getMatchId() { return matchId; }
    public void setMatchId(UUID matchId) { this.matchId = matchId; }

    public int getGolesLocal() { return golesLocal; }
    public void setGolesLocal(int golesLocal) { this.golesLocal = golesLocal; }

    public int getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(int golesVisitante) { this.golesVisitante = golesVisitante; }

    public Instant getFechaConfirmacion() { return fechaConfirmacion; }
    public void setFechaConfirmacion(Instant fechaConfirmacion) { this.fechaConfirmacion = fechaConfirmacion; }

    public String getConfirmadoPor() { return confirmadoPor; }
    public void setConfirmadoPor(String confirmadoPor) { this.confirmadoPor = confirmadoPor; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
