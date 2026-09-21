package com.sportflow.features.competitions;

import com.sportflow.core.errors.ConflictException;
import com.sportflow.features.competitions.application.dto.*;
import com.sportflow.features.competitions.application.usecases.ManagePhaseUseCase;
import com.sportflow.features.competitions.domain.model.*;
import com.sportflow.features.competitions.domain.ports.*;
import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.model.TeamGender;
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

class PhaseAndGroupUseCaseTest {

    private PhaseRepositoryPort phaseRepository;
    private TournamentRepositoryPort tournamentRepository;
    private CompetitionGroupRepositoryPort groupRepository;
    private TournamentRegistrationRepositoryPort registrationRepository;
    private TournamentBracketRepositoryPort bracketRepository;
    private TeamRepositoryPort teamRepository;
    private ManagePhaseUseCase useCase;

    @BeforeEach
    void setUp() {
        phaseRepository = Mockito.mock(PhaseRepositoryPort.class);
        tournamentRepository = Mockito.mock(TournamentRepositoryPort.class);
        groupRepository = Mockito.mock(CompetitionGroupRepositoryPort.class);
        registrationRepository = Mockito.mock(TournamentRegistrationRepositoryPort.class);
        bracketRepository = Mockito.mock(TournamentBracketRepositoryPort.class);
        teamRepository = Mockito.mock(TeamRepositoryPort.class);

        useCase = new ManagePhaseUseCase(
                phaseRepository,
                tournamentRepository,
                groupRepository,
                registrationRepository,
                bracketRepository,
                teamRepository
        );
    }

    @Test
    @DisplayName("HU-GC-03: Debe estructurar fases jerárquicas con subfases recursivas")
    void shouldCreatePhaseHierarchySuccessfully() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = Tournament.crear(UUID.randomUUID(), "Mundialito", "Fase previa",
                LocalDate.now().plusDays(10), LocalDate.now().plusMonths(2), LocalDate.now().plusDays(5), 16);

        Phase rootPhase = Phase.crear(tournamentId, "Fase Regular", PhaseType.LIGA, 1, null);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(phaseRepository.findById(rootPhase.getId())).thenReturn(Optional.of(rootPhase));
        when(phaseRepository.save(any(Phase.class))).thenAnswer(inv -> inv.getArgument(0));

        // Subfase con recursividad (fasePadreId)
        CreatePhaseRequest subPhaseRequest = new CreatePhaseRequest(
                "Subfase Ronda 1", PhaseType.GRUPOS, 2, rootPhase.getId()
        );

        PhaseResponse response = useCase.createPhase(tournamentId, subPhaseRequest);

        assertNotNull(response);
        assertEquals("Subfase Ronda 1", response.nombre());
        assertEquals(rootPhase.getId(), response.fasePadreId());
        verify(phaseRepository).save(any(Phase.class));
    }

    @Test
    @DisplayName("HU-GC-04: Debe asignar equipo a un grupo validando inscripción y evitando duplicidad en la misma fase")
    void shouldAssignTeamToGroupSuccessfully() {
        UUID tournamentId = UUID.randomUUID();
        UUID phaseId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();

        Phase phase = Phase.crear(tournamentId, "Fase de Grupos", PhaseType.GRUPOS, 1, null);
        CompetitionGroup group = CompetitionGroup.crear(phaseId, "Grupo A", 1);
        Team team = Team.crear(UUID.randomUUID(), "Real Sociedad", "San Sebastián", "Profesional", TeamGender.MASCULINO);

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(registrationRepository.existsByTournamentIdAndTeamId(tournamentId, teamId)).thenReturn(true);
        when(groupRepository.existsByPhaseIdAndEquipoId(phaseId, teamId)).thenReturn(false);

        useCase.assignTeamToGroup(phaseId, groupId, teamId);

        assertTrue(group.getEquipoIds().contains(teamId));
        verify(groupRepository).save(group);
    }

    @Test
    @DisplayName("HU-GC-04: Debe rechazar asignación si el equipo ya está asignado a otro grupo en la misma fase")
    void shouldRejectDuplicateTeamInSamePhase() {
        UUID tournamentId = UUID.randomUUID();
        UUID phaseId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();

        Phase phase = Phase.crear(tournamentId, "Fase de Grupos", PhaseType.GRUPOS, 1, null);
        CompetitionGroup group = CompetitionGroup.crear(phaseId, "Grupo B", 2);
        Team team = Team.crear(UUID.randomUUID(), "Real Sociedad", "San Sebastián", "Profesional", TeamGender.MASCULINO);

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(registrationRepository.existsByTournamentIdAndTeamId(tournamentId, teamId)).thenReturn(true);
        when(groupRepository.existsByPhaseIdAndEquipoId(phaseId, teamId)).thenReturn(true); // Ya asignado

        assertThrows(ConflictException.class, () -> useCase.assignTeamToGroup(phaseId, groupId, teamId));
        verify(groupRepository, never()).save(any(CompetitionGroup.class));
    }

    @Test
    @DisplayName("HU-GC-05: Debe crear llave eliminatoria (Playoff Bracket)")
    void shouldCreateBracketSuccessfully() {
        UUID phaseId = UUID.randomUUID();
        Phase phase = Phase.crear(UUID.randomUUID(), "Cuartos de Final", PhaseType.ELIMINATORIA_DIRECTA, 2, null);

        CreateBracketRequest request = new CreateBracketRequest("Llave 1 (1A vs 2B)", 1, 1, null);

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(bracketRepository.save(any(TournamentBracket.class))).thenAnswer(inv -> inv.getArgument(0));

        BracketResponse response = useCase.createBracket(phaseId, request);

        assertNotNull(response);
        assertEquals("Llave 1 (1A vs 2B)", response.nombre());
        assertEquals(1, response.ronda());
        verify(bracketRepository).save(any(TournamentBracket.class));
    }
}
