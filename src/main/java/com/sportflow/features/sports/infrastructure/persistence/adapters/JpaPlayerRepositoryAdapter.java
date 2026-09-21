package com.sportflow.features.sports.infrastructure.persistence.adapters;

import com.sportflow.features.sports.domain.model.Player;
import com.sportflow.features.sports.domain.ports.PlayerRepositoryPort;
import com.sportflow.features.sports.infrastructure.persistence.entities.PlayerEntity;
import com.sportflow.features.sports.infrastructure.persistence.repositories.SpringDataPlayerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaPlayerRepositoryAdapter implements PlayerRepositoryPort {

    private final SpringDataPlayerRepository repository;

    public JpaPlayerRepositoryAdapter(SpringDataPlayerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Player save(Player player) {
        PlayerEntity entity = new PlayerEntity(
                player.getId(),
                player.getPersonaId(),
                player.getTipoDocumento(),
                player.getNumeroIdentificacion(),
                player.getNombres(),
                player.getApellidos(),
                player.getFechaNacimiento(),
                player.getPosicion(),
                player.getEstado(),
                player.getCreadoEn(),
                player.getActualizadoEn()
        );
        PlayerEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Player> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Player> findByTipoAndNumeroIdentificacion(String tipoDocumento, String numeroIdentificacion) {
        return repository.findByTipoDocumentoAndNumeroIdentificacion(tipoDocumento, numeroIdentificacion)
                .map(this::toDomain);
    }

    @Override
    public List<Player> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Player toDomain(PlayerEntity entity) {
        return new Player(
                entity.getId(),
                entity.getPersonaId(),
                entity.getTipoDocumento(),
                entity.getNumeroIdentificacion(),
                entity.getNombres(),
                entity.getApellidos(),
                entity.getFechaNacimiento(),
                entity.getPosicion(),
                entity.getEstado(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }
}
