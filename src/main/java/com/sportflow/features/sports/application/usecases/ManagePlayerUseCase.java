package com.sportflow.features.sports.application.usecases;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.ConflictException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.sports.application.dto.CreatePlayerRequest;
import com.sportflow.features.sports.application.dto.PlayerResponse;
import com.sportflow.features.sports.application.dto.UpdatePlayerRequest;
import com.sportflow.features.sports.domain.model.Player;
import com.sportflow.features.sports.domain.ports.PlayerContractRepositoryPort;
import com.sportflow.features.sports.domain.ports.PlayerRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ManagePlayerUseCase {

    private final PlayerRepositoryPort playerRepositoryPort;
    private final PlayerContractRepositoryPort playerContractRepositoryPort;

    public ManagePlayerUseCase(PlayerRepositoryPort playerRepositoryPort,
                               PlayerContractRepositoryPort playerContractRepositoryPort) {
        this.playerRepositoryPort = playerRepositoryPort;
        this.playerContractRepositoryPort = playerContractRepositoryPort;
    }

    public PlayerResponse createPlayer(CreatePlayerRequest request) {
        playerRepositoryPort.findByTipoAndNumeroIdentificacion(request.tipoDocumento(), request.numeroIdentificacion())
                .ifPresent(existing -> {
                    throw new ConflictException("Ya existe un jugador registrado con el documento: " +
                            request.tipoDocumento() + " " + request.numeroIdentificacion());
                });

        Player player = Player.crear(
                request.personaId(),
                request.tipoDocumento(),
                request.numeroIdentificacion(),
                request.nombres(),
                request.apellidos(),
                request.fechaNacimiento(),
                request.posicion()
        );
        Player saved = playerRepositoryPort.save(player);
        return mapToResponse(saved);
    }

    public PlayerResponse updatePlayer(UUID id, UpdatePlayerRequest request) {
        Player player = playerRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jugador no encontrado con ID: " + id));

        player.actualizar(
                request.nombres(),
                request.apellidos(),
                request.fechaNacimiento(),
                request.posicion(),
                request.estado()
        );
        Player saved = playerRepositoryPort.save(player);
        return mapToResponse(saved);
    }

    public void deleteOrDeactivatePlayer(UUID id) {
        Player player = playerRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jugador no encontrado con ID: " + id));

        if (playerContractRepositoryPort.findActiveContractByPlayerId(id).isPresent()) {
            throw new BusinessRuleException("No se puede eliminar el jugador porque cuenta con un contrato activo con un equipo. " +
                    "Finalice el contrato previamente.");
        }

        playerRepositoryPort.deleteById(id);
    }

    @Transactional(readOnly = true)
    public PlayerResponse getPlayerById(UUID id) {
        Player player = playerRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Jugador no encontrado con ID: " + id));
        return mapToResponse(player);
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> listPlayers() {
        return playerRepositoryPort.findAll().stream().map(this::mapToResponse).toList();
    }

    private PlayerResponse mapToResponse(Player player) {
        return new PlayerResponse(
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
    }
}
