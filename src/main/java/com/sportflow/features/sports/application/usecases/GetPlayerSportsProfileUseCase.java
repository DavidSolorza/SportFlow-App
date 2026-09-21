package com.sportflow.features.sports.application.usecases;

import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.sports.application.dto.ContractResponse;
import com.sportflow.features.sports.application.dto.PlayerProfileResponse;
import com.sportflow.features.sports.application.dto.PlayerResponse;
import com.sportflow.features.sports.application.dto.PlayerSkillResponse;
import com.sportflow.features.sports.domain.model.Player;
import com.sportflow.features.sports.domain.model.PlayerContract;
import com.sportflow.features.sports.domain.model.Skill;
import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.ports.PlayerContractRepositoryPort;
import com.sportflow.features.sports.domain.ports.PlayerRepositoryPort;
import com.sportflow.features.sports.domain.ports.PlayerSkillRepositoryPort;
import com.sportflow.features.sports.domain.ports.SkillRepositoryPort;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetPlayerSportsProfileUseCase {

    private final PlayerRepositoryPort playerRepositoryPort;
    private final PlayerContractRepositoryPort contractRepositoryPort;
    private final TeamRepositoryPort teamRepositoryPort;
    private final PlayerSkillRepositoryPort playerSkillRepositoryPort;
    private final SkillRepositoryPort skillRepositoryPort;

    public GetPlayerSportsProfileUseCase(PlayerRepositoryPort playerRepositoryPort,
                                         PlayerContractRepositoryPort contractRepositoryPort,
                                         TeamRepositoryPort teamRepositoryPort,
                                         PlayerSkillRepositoryPort playerSkillRepositoryPort,
                                         SkillRepositoryPort skillRepositoryPort) {
        this.playerRepositoryPort = playerRepositoryPort;
        this.contractRepositoryPort = contractRepositoryPort;
        this.teamRepositoryPort = teamRepositoryPort;
        this.playerSkillRepositoryPort = playerSkillRepositoryPort;
        this.skillRepositoryPort = skillRepositoryPort;
    }

    public PlayerProfileResponse getPlayerSportsProfile(UUID playerId) {
        Player player = playerRepositoryPort.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Jugador no encontrado con ID: " + playerId));

        PlayerResponse playerDTO = new PlayerResponse(
                player.getId(),
                player.getPersonaId(),
                player.getTipoDocumento(),
                player.getNumeroIdentificacion(),
                player.getNombres(),
                player.getApellidos(),
                player.getNombreCompleto(),
                player.getFechaNacimiento(),
                player.getPosicion(),
                player.getEstado()
        );

        // Contrato activo actual
        Optional<PlayerContract> activeContractOpt = contractRepositoryPort.findActiveContractByPlayerId(playerId);
        ContractResponse equipoActualDTO = activeContractOpt.map(this::mapContractToResponse).orElse(null);

        // Historial completo de traspasos
        List<ContractResponse> historialTraspasos = contractRepositoryPort
                .findByPlayerIdOrderByFechaInicioDesc(playerId)
                .stream()
                .map(this::mapContractToResponse)
                .toList();

        // Habilidades asignadas
        List<PlayerSkillResponse> habilidadesDTO = playerSkillRepositoryPort.findByPlayerId(playerId).stream().map(ps -> {
            Skill s = skillRepositoryPort.findById(ps.getSkillId()).orElse(null);
            return new PlayerSkillResponse(
                    ps.getSkillId(),
                    s != null ? s.getNombreCanonico() : "Habilidad",
                    ps.getNivel(),
                    ps.getObservacion(),
                    ps.getFechaRegistro()
            );
        }).toList();

        return new PlayerProfileResponse(playerDTO, equipoActualDTO, historialTraspasos, habilidadesDTO);
    }

    private ContractResponse mapContractToResponse(PlayerContract c) {
        Team t = teamRepositoryPort.findById(c.getTeamId()).orElse(null);
        return new ContractResponse(
                c.getId(),
                c.getPlayerId(),
                c.getTeamId(),
                t != null ? t.getNombreDistintivo() : "Equipo",
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getNumeroCamiseta(),
                c.getEstado(),
                c.getObservaciones()
        );
    }
}
