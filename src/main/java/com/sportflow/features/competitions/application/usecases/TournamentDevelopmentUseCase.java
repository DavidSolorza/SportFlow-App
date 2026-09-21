package com.sportflow.features.competitions.application.usecases;

import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.competitions.application.dto.*;
import com.sportflow.features.competitions.domain.model.*;
import com.sportflow.features.competitions.domain.ports.*;
import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TournamentDevelopmentUseCase {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final PhaseRepositoryPort phaseRepositoryPort;
    private final CompetitionGroupRepositoryPort groupRepositoryPort;
    private final MatchRepositoryPort matchRepositoryPort;
    private final MatchResultRepositoryPort matchResultRepositoryPort;
    private final StandingsRepositoryPort standingsRepositoryPort;
    private final TournamentBracketRepositoryPort bracketRepositoryPort;
    private final TeamRepositoryPort teamRepositoryPort;

    public TournamentDevelopmentUseCase(TournamentRepositoryPort tournamentRepositoryPort,
                                        PhaseRepositoryPort phaseRepositoryPort,
                                        CompetitionGroupRepositoryPort groupRepositoryPort,
                                        MatchRepositoryPort matchRepositoryPort,
                                        MatchResultRepositoryPort matchResultRepositoryPort,
                                        StandingsRepositoryPort standingsRepositoryPort,
                                        TournamentBracketRepositoryPort bracketRepositoryPort,
                                        TeamRepositoryPort teamRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.phaseRepositoryPort = phaseRepositoryPort;
        this.groupRepositoryPort = groupRepositoryPort;
        this.matchRepositoryPort = matchRepositoryPort;
        this.matchResultRepositoryPort = matchResultRepositoryPort;
        this.standingsRepositoryPort = standingsRepositoryPort;
        this.bracketRepositoryPort = bracketRepositoryPort;
        this.teamRepositoryPort = teamRepositoryPort;
    }

    /**
     * HU-GC-08: Cálculo determinista y automático de la tabla de posiciones a partir de los marcadores oficiales.
     */
    public List<StandingsResponse> calculateGroupStandings(UUID phaseId, UUID groupId) {
        CompetitionGroup group = groupRepositoryPort.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado con ID: " + groupId));

        List<Match> groupMatches = matchRepositoryPort.findByGroupId(groupId)
                .stream()
                .filter(m -> m.getEstado() == MatchStatus.FINALIZADO && m.isResultadoConfirmado())
                .toList();

        Map<UUID, StandingsEntry> statsMap = new HashMap<>();

        // Inicializar todas las escuadras pertenecientes al grupo
        for (UUID teamId : group.getEquipoIds()) {
            statsMap.put(teamId, StandingsEntry.inicial(phaseId, groupId, teamId));
        }

        // Acumular estadísticas de cada partido finalizado
        for (Match match : groupMatches) {
            Optional<MatchResult> resultOpt = matchResultRepositoryPort.findByMatchId(match.getId());
            if (resultOpt.isPresent()) {
                MatchResult r = resultOpt.get();
                UUID localId = match.getEquipoLocalId();
                UUID visitanteId = match.getEquipoVisitanteId();

                if (localId != null && statsMap.containsKey(localId)) {
                    statsMap.get(localId).agregarResultado(r.getGolesLocal(), r.getGolesVisitante());
                }
                if (visitanteId != null && statsMap.containsKey(visitanteId)) {
                    statsMap.get(visitanteId).agregarResultado(r.getGolesVisitante(), r.getGolesLocal());
                }
            }
        }

        // Ordenar por: Puntos DESC, Diferencia de Goles DESC, Goles a Favor DESC
        List<StandingsEntry> sortedList = new ArrayList<>(statsMap.values());
        sortedList.sort(Comparator
                .comparingInt(StandingsEntry::getPuntos)
                .thenComparingInt(StandingsEntry::getDiferenciaGoles)
                .thenComparingInt(StandingsEntry::getGolesFavor)
                .reversed());

        int posicion = 1;
        for (StandingsEntry entry : sortedList) {
            entry.setPosicion(posicion++);
        }

        standingsRepositoryPort.saveAll(sortedList);

        return sortedList.stream().map(this::mapStandingsToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<StandingsResponse> getStandingsByPhaseAndGroup(UUID phaseId, UUID groupId) {
        if (groupId != null) {
            return standingsRepositoryPort
                    .findByPhaseIdAndGroupIdOrderByPuntosDescDiferenciaGolesDescGolesFavorDesc(phaseId, groupId)
                    .stream()
                    .map(this::mapStandingsToResponse)
                    .toList();
        }
        return standingsRepositoryPort
                .findByPhaseIdOrderByPuntosDescDiferenciaGolesDescGolesFavorDesc(phaseId)
                .stream()
                .map(this::mapStandingsToResponse)
                .toList();
    }

    /**
     * HU-GC-08: Vista centralizada ejecutiva del desarrollo del torneo
     */
    @Transactional(readOnly = true)
    public TournamentDevelopmentResponse getTournamentDevelopment(UUID tournamentId) {
        Tournament tournament = tournamentRepositoryPort.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Torneo no encontrado con ID: " + tournamentId));

        List<Phase> phases = phaseRepositoryPort.findByTournamentIdOrderByOrdenAsc(tournamentId);

        List<TournamentDevelopmentResponse.PhaseDevelopmentDTO> phaseDTOs = phases.stream().map(phase -> {
            List<CompetitionGroup> groups = groupRepositoryPort.findByPhaseIdOrderByOrdenAsc(phase.getId());
            List<TournamentDevelopmentResponse.GroupDevelopmentDTO> groupDTOs = groups.stream().map(group -> {
                List<StandingsResponse> standings = getStandingsByPhaseAndGroup(phase.getId(), group.getId());
                return new TournamentDevelopmentResponse.GroupDevelopmentDTO(group.getId(), group.getNombre(), standings);
            }).toList();

            List<BracketResponse> brackets = bracketRepositoryPort
                    .findByPhaseIdOrderByRondaAscOrdenAsc(phase.getId())
                    .stream()
                    .map(b -> new BracketResponse(b.getId(), b.getPhaseId(), b.getNombre(), b.getRonda(), b.getOrden(), b.getPartidoId(), b.getGanadorEquipoId()))
                    .toList();

            List<MatchResponse> matches = matchRepositoryPort.findByPhaseId(phase.getId())
                    .stream()
                    .map(this::mapMatchToResponse)
                    .toList();

            return new TournamentDevelopmentResponse.PhaseDevelopmentDTO(
                    phase.getId(),
                    phase.getNombre(),
                    phase.getTipo().name(),
                    phase.getOrden(),
                    phase.getEstado().name(),
                    groupDTOs,
                    brackets,
                    matches
            );
        }).toList();

        return new TournamentDevelopmentResponse(
                tournament.getId(),
                tournament.getNombre(),
                tournament.getEstado(),
                phaseDTOs
        );
    }

    private StandingsResponse mapStandingsToResponse(StandingsEntry s) {
        Team t = teamRepositoryPort.findById(s.getTeamId()).orElse(null);
        return new StandingsResponse(
                s.getId(),
                s.getPhaseId(),
                s.getGroupId(),
                s.getTeamId(),
                t != null ? t.getNombreDistintivo() : "Equipo",
                s.getPosicion(),
                s.getPartidosJugados(),
                s.getVictorias(),
                s.getEmpates(),
                s.getDerrotas(),
                s.getGolesFavor(),
                s.getGolesContra(),
                s.getDiferenciaGoles(),
                s.getPuntos(),
                s.getFechaCalculo()
        );
    }

    private MatchResponse mapMatchToResponse(Match m) {
        Team local = m.getEquipoLocalId() != null ? teamRepositoryPort.findById(m.getEquipoLocalId()).orElse(null) : null;
        Team visitante = m.getEquipoVisitanteId() != null ? teamRepositoryPort.findById(m.getEquipoVisitanteId()).orElse(null) : null;
        MatchResult result = matchResultRepositoryPort.findByMatchId(m.getId()).orElse(null);
        MatchResultResponse resultDTO = result != null
                ? new MatchResultResponse(result.getId(), result.getMatchId(), result.getGolesLocal(), result.getGolesVisitante(), result.getFechaConfirmacion(), result.getConfirmadoPor(), result.getObservaciones())
                : null;

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
