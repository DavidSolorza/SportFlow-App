package com.sportflow.features.competitions.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clasificaciones")
public class StandingsEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "fase_id", nullable = false)
    private UUID phaseId;

    @Column(name = "grupo_id")
    private UUID groupId;

    @Column(name = "equipo_id", nullable = false)
    private UUID teamId;

    @Column(name = "partidos_jugados", nullable = false)
    private int partidosJugados;

    @Column(name = "victorias", nullable = false)
    private int victorias;

    @Column(name = "empates", nullable = false)
    private int empates;

    @Column(name = "derrotas", nullable = false)
    private int derrotas;

    @Column(name = "goles_favor", nullable = false)
    private int golesFavor;

    @Column(name = "goles_contra", nullable = false)
    private int golesContra;

    @Column(name = "diferencia_goles", nullable = false)
    private int diferenciaGoles;

    @Column(name = "puntos", nullable = false)
    private int puntos;

    @Column(name = "posicion", nullable = false)
    private int posicion;

    @Column(name = "fecha_calculo", nullable = false)
    private Instant fechaCalculo;

    public StandingsEntity() {}

    public StandingsEntity(UUID id, UUID phaseId, UUID groupId, UUID teamId,
                           int partidosJugados, int victorias, int empates, int derrotas,
                           int golesFavor, int golesContra, int diferenciaGoles,
                           int puntos, int posicion, Instant fechaCalculo) {
        this.id = id;
        this.phaseId = phaseId;
        this.groupId = groupId;
        this.teamId = teamId;
        this.partidosJugados = partidosJugados;
        this.victorias = victorias;
        this.empates = empates;
        this.derrotas = derrotas;
        this.golesFavor = golesFavor;
        this.golesContra = golesContra;
        this.diferenciaGoles = diferenciaGoles;
        this.puntos = puntos;
        this.posicion = posicion;
        this.fechaCalculo = fechaCalculo;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getPhaseId() { return phaseId; }
    public void setPhaseId(UUID phaseId) { this.phaseId = phaseId; }

    public UUID getGroupId() { return groupId; }
    public void setGroupId(UUID groupId) { this.groupId = groupId; }

    public UUID getTeamId() { return teamId; }
    public void setTeamId(UUID teamId) { this.teamId = teamId; }

    public int getPartidosJugados() { return partidosJugados; }
    public void setPartidosJugados(int partidosJugados) { this.partidosJugados = partidosJugados; }

    public int getVictorias() { return victorias; }
    public void setVictorias(int victorias) { this.victorias = victorias; }

    public int getEmpates() { return empates; }
    public void setEmpates(int empates) { this.empates = empates; }

    public int getDerrotas() { return derrotas; }
    public void setDerrotas(int derrotas) { this.derrotas = derrotas; }

    public int getGolesFavor() { return golesFavor; }
    public void setGolesFavor(int golesFavor) { this.golesFavor = golesFavor; }

    public int getGolesContra() { return golesContra; }
    public void setGolesContra(int golesContra) { this.golesContra = golesContra; }

    public int getDiferenciaGoles() { return diferenciaGoles; }
    public void setDiferenciaGoles(int diferenciaGoles) { this.diferenciaGoles = diferenciaGoles; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public int getPosicion() { return posicion; }
    public void setPosicion(int posicion) { this.posicion = posicion; }

    public Instant getFechaCalculo() { return fechaCalculo; }
    public void setFechaCalculo(Instant fechaCalculo) { this.fechaCalculo = fechaCalculo; }
}
