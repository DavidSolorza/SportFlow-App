package com.sportflow.features.sports.domain.ports;

import com.sportflow.features.sports.domain.model.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepositoryPort {
    Player save(Player player);
    Optional<Player> findById(UUID id);
    Optional<Player> findByTipoAndNumeroIdentificacion(String tipoDocumento, String numeroIdentificacion);
    List<Player> findAll();
    void deleteById(UUID id);
}
