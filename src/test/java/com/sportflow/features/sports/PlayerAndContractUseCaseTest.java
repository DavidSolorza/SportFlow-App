package com.sportflow.features.sports;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.ConflictException;
import com.sportflow.features.sports.application.dto.*;
import com.sportflow.features.sports.application.usecases.GetPlayerSportsProfileUseCase;
import com.sportflow.features.sports.application.usecases.ManagePlayerContractUseCase;
import com.sportflow.features.sports.application.usecases.ManagePlayerUseCase;
import com.sportflow.features.sports.application.usecases.ManageSkillUseCase;
import com.sportflow.features.sports.domain.model.*;
import com.sportflow.features.sports.domain.ports.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlayerAndContractUseCaseTest {

    private PlayerRepositoryPort playerRepository;
    private PlayerContractRepositoryPort contractRepository;
    private TeamRepositoryPort teamRepository;
    private SkillRepositoryPort skillRepository;
    private PlayerSkillRepositoryPort playerSkillRepository;

    private ManagePlayerUseCase playerUseCase;
    private ManagePlayerContractUseCase contractUseCase;
    private ManageSkillUseCase skillUseCase;
    private GetPlayerSportsProfileUseCase profileUseCase;

    @BeforeEach
    void setUp() {
        playerRepository = Mockito.mock(PlayerRepositoryPort.class);
        contractRepository = Mockito.mock(PlayerContractRepositoryPort.class);
        teamRepository = Mockito.mock(TeamRepositoryPort.class);
        skillRepository = Mockito.mock(SkillRepositoryPort.class);
        playerSkillRepository = Mockito.mock(PlayerSkillRepositoryPort.class);

        playerUseCase = new ManagePlayerUseCase(playerRepository, contractRepository);
        contractUseCase = new ManagePlayerContractUseCase(contractRepository, playerRepository, teamRepository);
        skillUseCase = new ManageSkillUseCase(skillRepository, playerSkillRepository, playerRepository);
        profileUseCase = new GetPlayerSportsProfileUseCase(playerRepository, contractRepository, teamRepository, playerSkillRepository, skillRepository);
    }

    @Test
    @DisplayName("HU-GD-04: Debe registrar un jugador deportivo validando documento único")
    void shouldRegisterPlayerSuccessfully() {
        CreatePlayerRequest request = new CreatePlayerRequest(
                null, "CC", "1020304050", "Lionel", "Cuccittini",
                LocalDate.of(1995, 6, 24), "Delantero"
        );

        when(playerRepository.findByTipoAndNumeroIdentificacion("CC", "1020304050")).thenReturn(Optional.empty());
        when(playerRepository.save(any(Player.class))).thenAnswer(inv -> inv.getArgument(0));

        PlayerResponse response = playerUseCase.createPlayer(request);

        assertNotNull(response);
        assertEquals("Lionel", response.nombres());
        assertEquals(PlayerStatus.ACTIVO, response.estado());
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    @DisplayName("HU-GD-04: Debe rechazar jugador si el documento ya se encuentra registrado")
    void shouldRejectDuplicatePlayerDocument() {
        CreatePlayerRequest request = new CreatePlayerRequest(
                null, "CC", "1020304050", "Lionel", "Cuccittini",
                LocalDate.of(1995, 6, 24), "Delantero"
        );

        when(playerRepository.findByTipoAndNumeroIdentificacion("CC", "1020304050"))
                .thenReturn(Optional.of(Player.crear(null, "CC", "1020304050", "Otro", "Nombre", LocalDate.now(), "Delantero")));

        assertThrows(ConflictException.class, () -> playerUseCase.createPlayer(request));
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    @DisplayName("HU-GD-05: Fichaje debe cerrar contrato anterior y abrir nuevo manteniendo historial inmutable")
    void shouldClosePreviousContractWhenTransferringPlayer() {
        UUID playerId = UUID.randomUUID();
        UUID oldTeamId = UUID.randomUUID();
        UUID newTeamId = UUID.randomUUID();

        Player player = Player.crear(null, "CC", "1020304050", "Lionel", "Cuccittini", LocalDate.of(1995, 6, 24), "Extremo");
        Team oldTeam = Team.crear(UUID.randomUUID(), "Equipo Anterior", "Ciudad A", "Profesional", TeamGender.MASCULINO);
        Team newTeam = Team.crear(UUID.randomUUID(), "Equipo Nuevo", "Ciudad B", "Profesional", TeamGender.MASCULINO);

        PlayerContract oldContract = PlayerContract.crear(playerId, oldTeamId, LocalDate.of(2022, 1, 1), LocalDate.of(2025, 12, 31), 10, "Contrato inicial");

        CreateContractRequest request = new CreateContractRequest(
                newTeamId, LocalDate.of(2024, 6, 1), LocalDate.of(2027, 6, 1), 10, "Traspaso de verano"
        );

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(teamRepository.findById(newTeamId)).thenReturn(Optional.of(newTeam));
        when(contractRepository.findActiveContractByPlayerId(playerId)).thenReturn(Optional.of(oldContract));
        when(contractRepository.save(any(PlayerContract.class))).thenAnswer(inv -> inv.getArgument(0));

        ContractResponse response = contractUseCase.assignPlayerToTeam(playerId, request);

        assertNotNull(response);
        assertEquals(ContractStatus.ACTIVO, response.estado());
        assertEquals("Equipo Nuevo", response.nombreEquipo());

        // Verificar que el contrato anterior fue finalizado formalmente con su motivo
        assertEquals(ContractStatus.FINALIZADO, oldContract.getEstado());
        assertTrue(oldContract.getObservaciones().contains("Traspaso al equipo Equipo Nuevo"));
        verify(contractRepository, times(2)).save(any(PlayerContract.class));
    }

    @Test
    @DisplayName("HU-GD-06 y HU-GD-07: Asignar habilidad técnica al jugador y consultar perfil")
    void shouldAssignSkillSuccessfully() {
        UUID playerId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();

        Player player = Player.crear(null, "CC", "1020304050", "Lionel", "Cuccittini", LocalDate.of(1995, 6, 24), "Extremo");
        Skill skill = Skill.crear("Definición", "Precisión de tiro al arco");

        AssignSkillRequest request = new AssignSkillRequest(skillId, "AVANZADO", "Nivel de élite mundial");

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(playerSkillRepository.findByPlayerIdAndSkillId(playerId, skillId)).thenReturn(Optional.empty());
        when(playerSkillRepository.save(any(PlayerSkill.class))).thenAnswer(inv -> inv.getArgument(0));

        PlayerSkillResponse response = skillUseCase.assignSkillToPlayer(playerId, request);

        assertNotNull(response);
        assertEquals("AVANZADO", response.nivel());
        assertEquals("Definición", response.nombreHabilidad());
    }

    @Test
    @DisplayName("HU-GD-08: Debe consolidar el perfil deportivo centralizado del jugador con equipo y habilidades")
    void shouldReturnCentralizedSportsProfile() {
        UUID playerId = UUID.randomUUID();
        UUID teamId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();

        Player player = Player.crear(null, "CC", "1020304050", "Lionel", "Cuccittini", LocalDate.of(1995, 6, 24), "Extremo");
        Team team = Team.crear(UUID.randomUUID(), "FC Barcelona", "Barcelona", "Profesional", TeamGender.MASCULINO);
        PlayerContract contract = PlayerContract.crear(playerId, teamId, LocalDate.of(2023, 1, 1), LocalDate.of(2026, 12, 31), 10, "Contrato titular");
        Skill skill = Skill.crear("Regate", "Habilidad de drible");
        PlayerSkill playerSkill = PlayerSkill.crear(playerId, skillId, "AVANZADO", "Drible magistral");

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
        when(contractRepository.findActiveContractByPlayerId(playerId)).thenReturn(Optional.of(contract));
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(contractRepository.findByPlayerIdOrderByFechaInicioDesc(playerId)).thenReturn(List.of(contract));
        when(playerSkillRepository.findByPlayerId(playerId)).thenReturn(List.of(playerSkill));
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));

        PlayerProfileResponse profile = profileUseCase.getPlayerSportsProfile(playerId);

        assertNotNull(profile);
        assertEquals("Lionel Cuccittini", profile.jugador().nombreCompleto());
        assertNotNull(profile.equipoActual());
        assertEquals("FC Barcelona", profile.equipoActual().nombreEquipo());
        assertEquals(1, profile.habilidades().size());
        assertEquals("Regate", profile.habilidades().get(0).nombreHabilidad());
    }
}
