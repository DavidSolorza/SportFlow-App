package com.sportflow.features.competitions;

import com.sportflow.features.competitions.application.dto.*;
import com.sportflow.features.competitions.application.usecases.ManageMatchUseCase;
import com.sportflow.features.competitions.application.usecases.TournamentDevelopmentUseCase;
import com.sportflow.features.competitions.domain.model.*;
import com.sportflow.features.competitions.domain.ports.*;
import com.sportflow.features.sports.domain.model.Player;
import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.model.TeamGender;
import com.sportflow.features.sports.domain.ports.PlayerRepositoryPort;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MatchAndDevelopmentUseCaseTest {

    private MatchRepositoryPort matchRepository;
    private MatchResultRepositoryPort matchResultRepository;
    private MatchEventRepositoryPort matchEventRepository;
    private PhaseRepositoryPort phaseRepository;
    private TeamRepositoryPort teamRepository;
    private PlayerRepositoryPort playerRepository;
    private TournamentBracketRepositoryPort bracketRepository;
    private TournamentRepositoryPort tournamentRepository;
    private CompetitionGroupRepositoryPort groupRepository;
    private StandingsRepositoryPort standingsRepository;

    private ManageMatchUseCase matchUseCase;
    private TournamentDevelopmentUseCase developmentUseCase;

    @BeforeEach
    void setUp() {
        matchRepository = Mockito.mock(MatchRepositoryPort.class);
        matchResultRepository = Mockito.mock(MatchResultRepositoryPort.class);
        matchEventRepository = Mockito.mock(MatchEventRepositoryPort.class);
        phaseRepository = Mockito.mock(PhaseRepositoryPort.class);
        teamRepository = Mockito.mock(TeamRepositoryPort.class);
        playerRepository = Mockito.mock(PlayerRepositoryPort.class);
        bracketRepository = Mockito.mock(TournamentBracketRepositoryPort.class);
        tournamentRepository = Mockito.mock(TournamentRepositoryPort.class);
        groupRepository = Mockito.mock(CompetitionGroupRepositoryPort.class);
        standingsRepository = Mockito.mock(StandingsRepositoryPort.class);

        developmentUseCase = new TournamentDevelopmentUseCase(
                tournamentRepository,
                phaseRepository,
                groupRepository,
                matchRepository,
                matchResultRepository,
                standingsRepository,
                bracketRepository,
                teamRepository
        );

        matchUseCase = new ManageMatchUseCase(
                matchRepository,
                matchResultRepository,
                matchEventRepository,
                phaseRepository,
                teamRepository,
                playerRepository,
                bracketRepository,
                developmentUseCase
        );
    }

    @Test
    @DisplayName("HU-GC-06: Debe programar un partido deportivo en fecha y escenario válido")
    void shouldScheduleMatchSuccessfully() {
        UUID phaseId = UUID.randomUUID();
        UUID localId = UUID.randomUUID();
        UUID visitanteId = UUID.randomUUID();

        Phase phase = Phase.crear(UUID.randomUUID(), "Fase Grupos", PhaseType.GRUPOS, 1, null);
        Team local = Team.crear(UUID.randomUUID(), "Equipo A", "Bogotá", "Profesional", TeamGender.MASCULINO);
        Team visitante = Team.crear(UUID.randomUUID(), "Equipo B", "Bogotá", "Profesional", TeamGender.MASCULINO);

        ScheduleMatchRequest request = new ScheduleMatchRequest(
                phaseId, null, localId, visitanteId, Instant.now().plusSeconds(3600), "Estadio Metropolitano", null, null
        );

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(teamRepository.findById(localId)).thenReturn(Optional.of(local));
        when(teamRepository.findById(visitanteId)).thenReturn(Optional.of(visitante));
        when(matchRepository.save(any(Match.class))).thenAnswer(inv -> inv.getArgument(0));

        MatchResponse response = matchUseCase.scheduleMatch(request);

        assertNotNull(response);
        assertEquals("Estadio Metropolitano", response.escenario());
        assertEquals("Equipo A", response.nombreEquipoLocal());
        assertEquals("Equipo B", response.nombreEquipoVisitante());
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    @DisplayName("HU-GC-07: Debe registrar resultado oficial, cambiar estado a FINALIZADO y confirmar marcador")
    void shouldRecordMatchResultSuccessfully() {
        UUID matchId = UUID.randomUUID();
        UUID localId = UUID.randomUUID();
        UUID visitanteId = UUID.randomUUID();

        Match match = Match.crear(UUID.randomUUID(), null, localId, visitanteId, Instant.now(), "Cancha 1", null, null);
        RecordMatchResultRequest request = new RecordMatchResultRequest(3, 1, "Árbitro Central", "Partido sin novedades");

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(matchResultRepository.save(any(MatchResult.class))).thenAnswer(inv -> inv.getArgument(0));
        when(matchRepository.save(any(Match.class))).thenAnswer(inv -> inv.getArgument(0));

        MatchResultResponse response = matchUseCase.recordMatchResult(matchId, request);

        assertNotNull(response);
        assertEquals(3, response.golesLocal());
        assertEquals(1, response.golesVisitante());
        assertEquals("Árbitro Central", response.confirmadoPor());
        assertTrue(match.isResultadoConfirmado());
        assertEquals(MatchStatus.FINALIZADO, match.getEstado());
        verify(matchResultRepository).save(any(MatchResult.class));
    }

    @Test
    @DisplayName("HU-GC-07: Debe registrar eventos del partido (goles, tarjetas, sustituciones) con minuto y jugador")
    void shouldRecordMatchEventSuccessfully() {
        UUID matchId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Match match = Match.crear(UUID.randomUUID(), null, teamId, UUID.randomUUID(), Instant.now(), "Cancha 1", null, null);
        Team team = Team.crear(UUID.randomUUID(), "Real FC", "Medellín", "Profesional", TeamGender.MASCULINO);
        Player player = Player.crear(null, "CC", "12345", "Mateo", "Gómez", LocalDate.of(1998, 1, 1), "Medio");

        RecordMatchEventRequest request = new RecordMatchEventRequest(
                playerId, teamId, EventType.GOL, 42, "Golazo de tiro libre desde 25 metros"
        );

        when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(matchEventRepository.save(any(MatchEvent.class))).thenAnswer(inv -> inv.getArgument(0));

        MatchEventResponse response = matchUseCase.recordMatchEvent(matchId, request);

        assertNotNull(response);
        assertEquals(EventType.GOL, response.tipoEvento());
        assertEquals(42, response.minuto());
        assertEquals("Mateo Gómez", response.nombreJugador());
        verify(matchEventRepository).save(any(MatchEvent.class));
    }

    @Test
    @DisplayName("HU-GC-08: Debe calcular deterministamente la tabla de posiciones con criterios FIFA")
    void shouldCalculateStandingsDeterministically() {
        UUID phaseId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID team1Id = UUID.randomUUID();
        UUID team2Id = UUID.randomUUID();

        CompetitionGroup group = new CompetitionGroup(groupId, phaseId, "Grupo 1", 1, Set.of(team1Id, team2Id), Instant.now());

        // Partido jugado entre team1 y team2: team1 ganó 2-0
        UUID matchId = UUID.randomUUID();
        Match match = new Match(matchId, phaseId, groupId, team1Id, team2Id, Instant.now(), "Estadio",
                MatchStatus.FINALIZADO, true, null, null, Instant.now(), Instant.now());
        MatchResult result = new MatchResult(UUID.randomUUID(), matchId, 2, 0, Instant.now(), "Árbitro", "OK");

        Team team1 = Team.crear(UUID.randomUUID(), "Equipo Líder", "Cali", "Pro", TeamGender.MASCULINO);
        Team team2 = Team.crear(UUID.randomUUID(), "Equipo Escolta", "Cali", "Pro", TeamGender.MASCULINO);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(matchRepository.findByGroupId(groupId)).thenReturn(List.of(match));
        when(matchResultRepository.findByMatchId(matchId)).thenReturn(Optional.of(result));
        when(teamRepository.findById(team1Id)).thenReturn(Optional.of(team1));
        when(teamRepository.findById(team2Id)).thenReturn(Optional.of(team2));

        List<StandingsResponse> standings = developmentUseCase.calculateGroupStandings(phaseId, groupId);

        assertNotNull(standings);
        assertEquals(2, standings.size());

        // El primer puesto debe ser el Equipo Líder con 3 puntos y +2 diferencia de gol
        StandingsResponse primero = standings.get(0);
        assertEquals(team1Id, primero.equipoId());
        assertEquals(1, primero.posicion());
        assertEquals(3, primero.puntos());
        assertEquals(1, primero.victorias());
        assertEquals(2, primero.diferenciaGoles());

        // El segundo puesto debe ser el Equipo Escolta con 0 puntos y -2 diferencia de gol
        StandingsResponse segundo = standings.get(1);
        assertEquals(team2Id, segundo.equipoId());
        assertEquals(2, segundo.posicion());
        assertEquals(0, segundo.puntos());
        assertEquals(1, segundo.derrotas());
        assertEquals(-2, segundo.diferenciaGoles());

        verify(standingsRepository).saveAll(anyList());
    }
}
