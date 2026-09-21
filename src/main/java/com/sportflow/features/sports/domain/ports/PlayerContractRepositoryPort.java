package com.sportflow.features.sports.domain.ports;

import com.sportflow.features.sports.domain.model.PlayerContract;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerContractRepositoryPort {
    PlayerContract save(PlayerContract contract);
    Optional<PlayerContract> findById(UUID id);
    Optional<PlayerContract> findActiveContractByPlayerId(UUID playerId);
    List<PlayerContract> findByPlayerIdOrderByFechaInicioDesc(UUID playerId);
    List<PlayerContract> findActiveContractsByTeamId(UUID teamId);
    List<PlayerContract> findAllByTeamId(UUID teamId);
    boolean existsActiveContractByPlayerIdAndTeamId(UUID playerId, UUID teamId);
}
