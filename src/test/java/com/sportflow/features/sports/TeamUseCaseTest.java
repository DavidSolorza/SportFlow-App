package com.sportflow.features.sports;

import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.sports.application.dto.CreateTeamRequest;
import com.sportflow.features.sports.application.dto.TeamResponse;
import com.sportflow.features.sports.application.usecases.ManageTeamUseCase;
import com.sportflow.features.sports.domain.model.Club;
import com.sportflow.features.sports.domain.model.Sport;
import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.model.TeamGender;
import com.sportflow.features.sports.domain.ports.ClubRepositoryPort;
import com.sportflow.features.sports.domain.ports.PlayerContractRepositoryPort;
import com.sportflow.features.sports.domain.ports.SportRepositoryPort;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TeamUseCaseTest {

    private TeamRepositoryPort teamRepository;
    private SportRepositoryPort sportRepository;
    private ClubRepositoryPort clubRepository;
    private PlayerContractRepositoryPort playerContractRepository;
    private ManageTeamUseCase useCase;

    @BeforeEach
    void setUp() {
        teamRepository = Mockito.mock(TeamRepositoryPort.class);
        sportRepository = Mockito.mock(SportRepositoryPort.class);
        clubRepository = Mockito.mock(ClubRepositoryPort.class);
        playerContractRepository = Mockito.mock(PlayerContractRepositoryPort.class);
        useCase = new ManageTeamUseCase(teamRepository, sportRepository, clubRepository, playerContractRepository);
    }

    @Test
    @DisplayName("HU-GD-02: Debe registrar un equipo deportivo asociado a un club")
    void shouldCreateTeamSuccessfully() {
        UUID clubId = UUID.randomUUID();
        Club club = Club.crear("Club Deportivo Los Álamos", "CDLA", "contacto@alamos.com");
        CreateTeamRequest request = new CreateTeamRequest(
                "Álamos Senior FC", "Bogotá", "Sub-20", TeamGender.MASCULINO, clubId
        );

        when(clubRepository.findById(clubId)).thenReturn(Optional.of(club));
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));

        TeamResponse response = useCase.createTeam(request);

        assertNotNull(response);
        assertEquals("Álamos Senior FC", response.nombreDistintivo());
        assertEquals(TeamGender.MASCULINO, response.genero());
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    @DisplayName("HU-GD-02: Debe rechazar equipo si el club especificado no existe")
    void shouldRejectWhenClubDoesNotExist() {
        UUID clubId = UUID.randomUUID();
        CreateTeamRequest request = new CreateTeamRequest(
                "Equipo Fantasma", "Medellín", "Libre", TeamGender.MIXTO, clubId
        );
        when(clubRepository.findById(clubId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> useCase.createTeam(request));
        verify(teamRepository, never()).save(any(Team.class));
    }

    @Test
    @DisplayName("HU-GD-03: Debe asociar múltiples disciplinas deportivas a un equipo (Relación M:N)")
    void shouldAssociateSportsToTeamSuccessfully() {
        UUID teamId = UUID.randomUUID();
        UUID sportId = UUID.randomUUID();
        Club club = Club.crear("Club Halcones", "HAL", "contacto@halcones.com");
        Team team = Team.crear(club.getId(), "Halcones Dorados", "Cali", "Sub-23", TeamGender.MASCULINO);
        Sport sport = Sport.crear("Baloncesto", "5 vs 5", null);

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(sportRepository.findById(sportId)).thenReturn(Optional.of(sport));
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.associateSportToTeam(teamId, sportId);

        assertTrue(team.practicaDeporte(sportId));
        verify(teamRepository).save(team);
    }
}
