package com.sportflow.features.sports.application.usecases;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.sports.application.dto.ContractResponse;
import com.sportflow.features.sports.application.dto.CreateContractRequest;
import com.sportflow.features.sports.application.dto.TerminateContractRequest;
import com.sportflow.features.sports.domain.model.Player;
import com.sportflow.features.sports.domain.model.PlayerContract;
import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.ports.PlayerContractRepositoryPort;
import com.sportflow.features.sports.domain.ports.PlayerRepositoryPort;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ManagePlayerContractUseCase {

    private final PlayerContractRepositoryPort contractRepositoryPort;
    private final PlayerRepositoryPort playerRepositoryPort;
    private final TeamRepositoryPort teamRepositoryPort;

    public ManagePlayerContractUseCase(PlayerContractRepositoryPort contractRepositoryPort,
                                       PlayerRepositoryPort playerRepositoryPort,
                                       TeamRepositoryPort teamRepositoryPort) {
        this.contractRepositoryPort = contractRepositoryPort;
        this.playerRepositoryPort = playerRepositoryPort;
        this.teamRepositoryPort = teamRepositoryPort;
    }

    public ContractResponse assignPlayerToTeam(UUID playerId, CreateContractRequest request) {
        Player player = playerRepositoryPort.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Jugador no encontrado con ID: " + playerId));

        Team team = teamRepositoryPort.findById(request.equipoId())
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + request.equipoId()));

        // Verificar si el jugador ya tiene un contrato activo con otro o con el mismo equipo
        Optional<PlayerContract> activeContractOpt = contractRepositoryPort.findActiveContractByPlayerId(playerId);
        if (activeContractOpt.isPresent()) {
            PlayerContract activeContract = activeContractOpt.get();
            if (activeContract.getTeamId().equals(request.equipoId())) {
                throw new BusinessRuleException("El jugador ya cuenta con un contrato activo con este mismo equipo.");
            }
            // Si viene de otro equipo, finalizamos el contrato previo para registrar el traspaso histórico
            activeContract.finalizarContrato(request.fechaInicio(), "Traspaso al equipo " + team.getNombreDistintivo());
            contractRepositoryPort.save(activeContract);
        }

        PlayerContract newContract = PlayerContract.crear(
                playerId,
                request.equipoId(),
                request.fechaInicio(),
                request.fechaFin(),
                request.numeroCamiseta(),
                request.observaciones()
        );

        PlayerContract saved = contractRepositoryPort.save(newContract);
        return mapToResponse(saved, team.getNombreDistintivo());
    }

    public ContractResponse terminateContract(UUID playerId, UUID contractId, TerminateContractRequest request) {
        PlayerContract contract = contractRepositoryPort.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado con ID: " + contractId));

        if (!contract.getPlayerId().equals(playerId)) {
            throw new BusinessRuleException("El contrato no pertenece al jugador especificado.");
        }

        contract.finalizarContrato(request.fechaFin(), request.motivo());
        PlayerContract saved = contractRepositoryPort.save(contract);

        Team team = teamRepositoryPort.findById(saved.getTeamId()).orElse(null);
        String nombreEquipo = team != null ? team.getNombreDistintivo() : "Equipo";
        return mapToResponse(saved, nombreEquipo);
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> getPlayerTransferHistory(UUID playerId) {
        playerRepositoryPort.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Jugador no encontrado con ID: " + playerId));

        List<PlayerContract> contracts = contractRepositoryPort.findByPlayerIdOrderByFechaInicioDesc(playerId);
        return contracts.stream().map(c -> {
            Team t = teamRepositoryPort.findById(c.getTeamId()).orElse(null);
            return mapToResponse(c, t != null ? t.getNombreDistintivo() : "Equipo");
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> getTeamRoster(UUID teamId) {
        Team team = teamRepositoryPort.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + teamId));

        List<PlayerContract> contracts = contractRepositoryPort.findActiveContractsByTeamId(teamId);
        return contracts.stream().map(c -> mapToResponse(c, team.getNombreDistintivo())).toList();
    }

    private ContractResponse mapToResponse(PlayerContract c, String nombreEquipo) {
        return new ContractResponse(
                c.getId(),
                c.getPlayerId(),
                c.getTeamId(),
                nombreEquipo,
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getNumeroCamiseta(),
                c.getEstado(),
                c.getObservaciones()
        );
    }
}
