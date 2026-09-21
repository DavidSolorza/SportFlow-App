package com.sportflow.features.competitions.application.usecases;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.competitions.application.dto.*;
import com.sportflow.features.competitions.domain.model.*;
import com.sportflow.features.competitions.domain.ports.*;
import com.sportflow.features.sports.domain.model.Player;
import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.ports.PlayerRepositoryPort;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ManageMatchUseCase {

    private final MatchRepositoryPort matchRepositoryPort;
    private final MatchResultRepositoryPort matchResultRepositoryPort;
    private final MatchEventRepositoryPort matchEventRepositoryPort;
    private final PhaseRepositoryPort phaseRepositoryPort;
    private final TeamRepositoryPort teamRepositoryPort;
    private final PlayerRepositoryPort playerRepositoryPort;
    private final TournamentBracketRepositoryPort bracketRepositoryPort;
    private final TournamentDevelopmentUseCase tournamentDevelopmentUseCase;

    public ManageMatchUseCase(MatchRepositoryPort matchRepositoryPort,
                              MatchResultRepositoryPort matchResultRepositoryPort,
                              MatchEventRepositoryPort matchEventRepositoryPort,
                              PhaseRepositoryPort phaseRepositoryPort,
                              TeamRepositoryPort teamRepositoryPort,
                              PlayerRepositoryPort playerRepositoryPort,
                              TournamentBracketRepositoryPort bracketRepositoryPort,
                              TournamentDevelopmentUseCase tournamentDevelopmentUseCase) {
        this.matchRepositoryPort = matchRepositoryPort;
        this.matchResultRepositoryPort = matchResultRepositoryPort;
        this.matchEventRepositoryPort = matchEventRepositoryPort;
        this.phaseRepositoryPort = phaseRepositoryPort;
        this.teamRepositoryPort = teamRepositoryPort;
        this.playerRepositoryPort = playerRepositoryPort;
        this.bracketRepositoryPort = bracketRepositoryPort;
        this.tournamentDevelopmentUseCase = tournamentDevelopmentUseCase;
    }

    // --- HU-GC-06: PROGRAMAR PARTIDOS ---

    public MatchResponse scheduleMatch(ScheduleMatchRequest request) {
        phaseRepositoryPort.findById(request.faseId())
                .orElseThrow(() -> new EntityNotFoundException("Fase no encontrada con ID: " + request.faseId()));

        if (request.equipoLocalId() != null) {
            teamRepositoryPort.findById(request.equipoLocalId())
                    .orElseThrow(() -> new EntityNotFoundException("Equipo local no encontrado con ID: " + request.equipoLocalId()));
        }

        if (request.equipoVisitanteId() != null) {
            teamRepositoryPort.findById(request.equipoVisitanteId())
                    .orElseThrow(() -> new EntityNotFoundException("Equipo visitante no encontrado con ID: " + request.equipoVisitanteId()));
        }

        Match match = Match.crear(
                request.faseId(),
                request.grupoId(),
                request.equipoLocalId(),
                request.equipoVisitanteId(),
                request.fechaHoraProgramada(),
                request.escenario(),
                request.partidoOrigen1(),
                request.partidoOrigen2()
        );

        Match saved = matchRepositoryPort.save(match);
        return mapMatchToResponse(saved);
    }

    // --- HU-GC-07: RESULTADOS Y EVENTOS ---

    public MatchResultResponse recordMatchResult(UUID matchId, RecordMatchResultRequest request) {
        Match match = matchRepositoryPort.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Partido no encontrado con ID: " + matchId));

        if (match.isResultadoConfirmado()) {
            throw new BusinessRuleException("El marcador de este partido ya fue confirmado oficialmente.");
        }

        if (match.getEquipoLocalId() == null || match.getEquipoVisitanteId() == null) {
            throw new BusinessRuleException("No se puede registrar resultado de un partido con equipos sin definir.");
        }

        MatchResult result = MatchResult.crear(
                matchId,
                request.golesLocal(),
                request.golesVisitante(),
                request.confirmadoPor(),
                request.observaciones()
        );

        MatchResult savedResult = matchResultRepositoryPort.save(result);
        match.confirmarFinalizacion();
        matchRepositoryPort.save(match);

        // 1. Si pertenece a un grupo, recalcular automáticamente la tabla de posiciones (HU-GC-08)
        if (match.getGroupId() != null) {
            tournamentDevelopmentUseCase.calculateGroupStandings(match.getPhaseId(), match.getGroupId());
        }

        // 2. Si pertenece a llaves eliminatorias, determinar ganador y propagar a la siguiente llave
        UUID ganadorId = null;
        if (request.golesLocal() > request.golesVisitante()) {
            ganadorId = match.getEquipoLocalId();
        } else if (request.golesVisitante() > request.golesLocal()) {
            ganadorId = match.getEquipoVisitanteId();
        }

        if (ganadorId != null) {
            final UUID finalGanador = ganadorId;
            // Actualizar llave si existe
            bracketRepositoryPort.findByPartidoId(matchId).ifPresent(bracket -> {
                bracket.registrarGanador(finalGanador);
                bracketRepositoryPort.save(bracket);
            });

            // Propagar al siguiente partido si hay cruce configurado
            List<Match> partidosSiguientes = matchRepositoryPort.findByPartidoOrigen(matchId);
            for (Match sig : partidosSiguientes) {
                if (matchId.equals(sig.getPartidoOrigen1())) {
                    sig.asignarEquipos(finalGanador, sig.getEquipoVisitanteId());
                } else if (matchId.equals(sig.getPartidoOrigen2())) {
                    sig.asignarEquipos(sig.getEquipoLocalId(), finalGanador);
                }
                matchRepositoryPort.save(sig);
            }
        }

        return new MatchResultResponse(
                savedResult.getId(),
                savedResult.getMatchId(),
                savedResult.getGolesLocal(),
                savedResult.getGolesVisitante(),
                savedResult.getFechaConfirmacion(),
                savedResult.getConfirmadoPor(),
                savedResult.getObservaciones()
        );
    }

    public MatchEventResponse recordMatchEvent(UUID matchId, RecordMatchEventRequest request) {
        Match match = matchRepositoryPort.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Partido no encontrado con ID: " + matchId));

        Team team = teamRepositoryPort.findById(request.equipoId())
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + request.equipoId()));

        String nombreJugador = null;
        if (request.jugadorId() != null) {
            Player player = playerRepositoryPort.findById(request.jugadorId())
                    .orElseThrow(() -> new EntityNotFoundException("Jugador no encontrado con ID: " + request.jugadorId()));
            nombreJugador = player.getNombreCompleto();
        }

        MatchEvent event = MatchEvent.crear(
                matchId,
                request.jugadorId(),
                request.equipoId(),
                request.tipoEvento(),
                request.minuto(),
                request.descripcion()
        );

        MatchEvent saved = matchEventRepositoryPort.save(event);

        return new MatchEventResponse(
                saved.getId(),
                saved.getMatchId(),
                saved.getJugadorId(),
                nombreJugador,
                saved.getEquipoId(),
                team.getNombreDistintivo(),
                saved.getTipoEvento(),
                saved.getMinuto(),
                saved.getDescripcion(),
                saved.getEstadoValidacion(),
                saved.getCreadoEn()
        );
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesByPhase(UUID phaseId) {
        return matchRepositoryPort.findByPhaseId(phaseId).stream().map(this::mapMatchToResponse).toList();
    }

    @Transactional(readOnly = true)
    public MatchResponse getMatchById(UUID id) {
        Match match = matchRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Partido no encontrado con ID: " + id));
        return mapMatchToResponse(match);
    }

    @Transactional(readOnly = true)
    public List<MatchEventResponse> getMatchEvents(UUID matchId) {
        matchRepositoryPort.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Partido no encontrado con ID: " + matchId));

        return matchEventRepositoryPort.findByMatchIdOrderByMinutoAsc(matchId).stream().map(e -> {
            String nombreJugador = e.getJugadorId() != null
                    ? playerRepositoryPort.findById(e.getJugadorId()).map(Player::getNombreCompleto).orElse("Jugador")
                    : "No registrado";
            String nombreEquipo = teamRepositoryPort.findById(e.getEquipoId()).map(Team::getNombreDistintivo).orElse("Equipo");

            return new MatchEventResponse(
                    e.getId(),
                    e.getMatchId(),
                    e.getJugadorId(),
                    nombreJugador,
                    e.getEquipoId(),
                    nombreEquipo,
                    e.getTipoEvento(),
                    e.getMinuto(),
                    e.getDescripcion(),
                    e.getEstadoValidacion(),
                    e.getCreadoEn()
            );
        }).toList();
    }

    private MatchResponse mapMatchToResponse(Match m) {
        Team local = m.getEquipoLocalId() != null ? teamRepositoryPort.findById(m.getEquipoLocalId()).orElse(null) : null;
        Team visitante = m.getEquipoVisitanteId() != null ? teamRepositoryPort.findById(m.getEquipoVisitanteId()).orElse(null) : null;
        Optional<MatchResult> resultOpt = matchResultRepositoryPort.findByMatchId(m.getId());
        MatchResultResponse resultDTO = resultOpt.map(r -> new MatchResultResponse(
                r.getId(), r.getMatchId(), r.getGolesLocal(), r.getGolesVisitante(),
                r.getFechaConfirmacion(), r.getConfirmadoPor(), r.getObservaciones()
        )).orElse(null);

        return new MatchResponse(
                m.getId(),
                m.getPhaseId(),
                m.getGroupId(),
                m.getEquipoLocalId(),
                local != null ? local.getNombreDistintivo() : "Por definir",
                m.getEquipoVisitanteId(),
                visitante != null ? visitante.getNombreDistintivo() : "Por definir",
                m.getFechaHoraProgramada(),
                m.getEscenario(),
                m.getEstado(),
                m.isResultadoConfirmado(),
                resultDTO
        );
    }
}
