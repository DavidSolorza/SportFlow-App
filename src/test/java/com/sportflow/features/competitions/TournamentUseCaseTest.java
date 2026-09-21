package com.sportflow.features.competitions;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.features.competitions.application.dto.CreateTournamentRequest;
import com.sportflow.features.competitions.application.dto.RegisterTeamRequest;
import com.sportflow.features.competitions.application.dto.RegistrationResponse;
import com.sportflow.features.competitions.application.dto.TournamentResponse;
import com.sportflow.features.competitions.application.usecases.ManageTournamentUseCase;
import com.sportflow.features.competitions.domain.model.RegistrationStatus;
import com.sportflow.features.competitions.domain.model.Tournament;
import com.sportflow.features.competitions.domain.model.TournamentRegistration;
import com.sportflow.features.competitions.domain.ports.PhaseRepositoryPort;
import com.sportflow.features.competitions.domain.ports.TournamentRegistrationRepositoryPort;
import com.sportflow.features.competitions.domain.ports.TournamentRepositoryPort;
import com.sportflow.features.sports.domain.model.Sport;
import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.model.TeamGender;
import com.sportflow.features.sports.domain.ports.SportRepositoryPort;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TournamentUseCaseTest {

    private TournamentRepositoryPort tournamentRepository;
    private TournamentRegistrationRepositoryPort registrationRepository;
    private SportRepositoryPort sportRepository;
    private TeamRepositoryPort teamRepository;
    private PhaseRepositoryPort phaseRepository;
    private ManageTournamentUseCase useCase;

    @BeforeEach
    void setUp() {
        tournamentRepository = Mockito.mock(TournamentRepositoryPort.class);
        registrationRepository = Mockito.mock(TournamentRegistrationRepositoryPort.class);
        sportRepository = Mockito.mock(SportRepositoryPort.class);
        teamRepository = Mockito.mock(TeamRepositoryPort.class);
        phaseRepository = Mockito.mock(PhaseRepositoryPort.class);

        useCase = new ManageTournamentUseCase(
                tournamentRepository,
                registrationRepository,
                sportRepository,
                teamRepository,
                phaseRepository
        );
    }

    @Test
    @DisplayName("HU-GC-01: Debe crear un torneo con fechas y cupo de participantes coherentes")
    void shouldCreateTournamentSuccessfully() {
        UUID sportId = UUID.randomUUID();
        CreateTournamentRequest request = new CreateTournamentRequest(
                "Copa Premier 2026", "Torneo apertura", sportId,
                LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(3),
                LocalDate.now().plusDays(20), 16
        );

        when(sportRepository.findById(sportId)).thenReturn(Optional.of(Sport.crear("Fútbol", "11v11", null)));
        when(tournamentRepository.save(any(Tournament.class))).thenAnswer(inv -> inv.getArgument(0));

        TournamentResponse response = useCase.createTournament(request);

        assertNotNull(response);
        assertEquals("Copa Premier 2026", response.nombre());
        assertEquals(16, response.cupoEquipos());
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    @DisplayName("HU-GC-01: Debe rechazar torneo si la fecha de fin es anterior a la fecha de inicio")
    void shouldRejectInvalidTournamentDates() {
        UUID sportId = UUID.randomUUID();
        CreateTournamentRequest request = new CreateTournamentRequest(
                "Torneo Inválido", "Error fechas", sportId,
                LocalDate.now().plusMonths(3), LocalDate.now().plusMonths(1),
                LocalDate.now().plusDays(10), 8
        );

        when(sportRepository.findById(sportId)).thenReturn(Optional.of(Sport.crear("Fútbol", "11v11", null)));

        assertThrows(BusinessRuleException.class, () -> useCase.createTournament(request));
    }

    @Test
    @DisplayName("HU-GC-02: Debe inscribir un equipo validando que practique el deporte y no exceda el cupo")
    void shouldRegisterTeamSuccessfully() {
        UUID sportId = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();

        Tournament tournament = Tournament.crear(
                sportId, "Torneo Regional", "Apertura",
                LocalDate.now().plusDays(10), LocalDate.now().plusMonths(2),
                LocalDate.now().plusDays(5), 4
        );

        Team team = Team.crear(UUID.randomUUID(), "Tiburones FC", "Barranquilla", "Profesional", TeamGender.MASCULINO);
        team.asociarDeporte(sportId);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(registrationRepository.existsByTournamentIdAndTeamId(tournamentId, teamId)).thenReturn(false);
        when(registrationRepository.countAcceptedByTournamentId(tournamentId)).thenReturn(2);
        when(registrationRepository.save(any(TournamentRegistration.class))).thenAnswer(inv -> inv.getArgument(0));

        RegisterTeamRequest request = new RegisterTeamRequest(teamId, "Inscripción en regla");
        RegistrationResponse response = useCase.registerTeam(tournamentId, request);

        assertNotNull(response);
        assertEquals(RegistrationStatus.ACEPTADA, response.estado());
        assertEquals("Tiburones FC", response.nombreEquipo());
        verify(registrationRepository).save(any(TournamentRegistration.class));
    }

    @Test
    @DisplayName("HU-GC-02: Debe rechazar inscripción si el equipo no practica el deporte del torneo")
    void shouldRejectWhenTeamDoesNotPracticeTournamentSport() {
        UUID tournamentSportId = UUID.randomUUID();
        UUID otherSportId = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();

        Tournament tournament = Tournament.crear(
                tournamentSportId, "Torneo Voleibol", "Playa",
                LocalDate.now().plusDays(10), LocalDate.now().plusMonths(2),
                LocalDate.now().plusDays(5), 4
        );

        Team team = Team.crear(UUID.randomUUID(), "Básquetbol Stars", "Medellín", "Profesional", TeamGender.MASCULINO);
        team.asociarDeporte(otherSportId); // No practica el deporte del torneo

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));

        RegisterTeamRequest request = new RegisterTeamRequest(teamId, "Inscripción errónea");

        assertThrows(BusinessRuleException.class, () -> useCase.registerTeam(tournamentId, request));
    }

    @Test
    @DisplayName("HU-GC-02: Debe rechazar inscripción si se superó el cupo máximo de equipos")
    void shouldRejectWhenTournamentQuotaExceeded() {
        UUID sportId = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();

        Tournament tournament = Tournament.crear(
                sportId, "Torneo Cuadrangular", "Rápido",
                LocalDate.now().plusDays(10), LocalDate.now().plusMonths(1),
                LocalDate.now().plusDays(5), 4
        );

        Team team = Team.crear(UUID.randomUUID(), "Águilas Doradas", "Rionegro", "Profesional", TeamGender.MASCULINO);
        team.asociarDeporte(sportId);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(registrationRepository.existsByTournamentIdAndTeamId(tournamentId, teamId)).thenReturn(false);
        when(registrationRepository.countAcceptedByTournamentId(tournamentId)).thenReturn(4); // Lleno

        RegisterTeamRequest request = new RegisterTeamRequest(teamId, "Cupo agotado");

        assertThrows(BusinessRuleException.class, () -> useCase.registerTeam(tournamentId, request));
    }
}
