package com.sportflow.features.competitions.domain.model;

import java.time.Instant;
import java.util.UUID;

public class StandingsEntry {
    private final UUID id;
    private final UUID phaseId;
    private final UUID groupId;
    private final UUID teamId;
    private int partidosJugados;
    private int victorias;
    private int empates;
    private int derrotas;
    private int golesFavor;
    private int golesContra;
    private int diferenciaGoles;
    private int puntos;
    private int posicion;
    private Instant fechaCalculo;

    public StandingsEntry(UUID id, UUID phaseId, UUID groupId, UUID teamId,
                          int partidosJugados, int victorias, int empates, int derrotas,
                          int golesFavor, int golesContra, int diferenciaGoles, int puntos,
                          int posicion, Instant fechaCalculo) {
        this.id = id != null ? id : UUID.randomUUID();
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
        this.fechaCalculo = fechaCalculo != null ? fechaCalculo : Instant.now();
    }

    public static StandingsEntry inicial(UUID phaseId, UUID groupId, UUID teamId) {
        return new StandingsEntry(UUID.randomUUID(), phaseId, groupId, teamId,
                0, 0, 0, 0, 0, 0, 0, 0, 1, Instant.now());
    }

    public void agregarResultado(int gf, int gc) {
        this.partidosJugados++;
        this.golesFavor += gf;
        this.golesContra += gc;
        this.diferenciaGoles = this.golesFavor - this.golesContra;

        if (gf > gc) {
            this.victorias++;
            this.puntos += 3;
        } else if (gf == gc) {
            this.empates++;
            this.puntos += 1;
        } else {
            this.derrotas++;
        }
        this.fechaCalculo = Instant.now();
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public UUID getId() { return id; }
    public UUID getPhaseId() { return phaseId; }
    public UUID getGroupId() { return groupId; }
    public UUID getTeamId() { return teamId; }
    public int getPartidosJugados() { return partidosJugados; }
    public int getVictorias() { return victorias; }
    public int getEmpates() { return empates; }
    public int getDerrotas() { return derrotas; }
    public int getGolesFavor() { return golesFavor; }
    public int getGolesContra() { return golesContra; }
    public int getDiferenciaGoles() { return diferenciaGoles; }
    public int getPuntos() { return puntos; }
    public int getPosicion() { return posicion; }
    public Instant getFechaCalculo() { return fechaCalculo; }
}
