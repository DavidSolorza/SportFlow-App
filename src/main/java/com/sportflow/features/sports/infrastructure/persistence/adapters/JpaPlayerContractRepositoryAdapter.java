package com.sportflow.features.sports.infrastructure.persistence.adapters;

import com.sportflow.features.sports.domain.model.ContractStatus;
import com.sportflow.features.sports.domain.model.PlayerContract;
import com.sportflow.features.sports.domain.ports.PlayerContractRepositoryPort;
import com.sportflow.features.sports.infrastructure.persistence.entities.PlayerContractEntity;
import com.sportflow.features.sports.infrastructure.persistence.repositories.SpringDataPlayerContractRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaPlayerContractRepositoryAdapter implements PlayerContractRepositoryPort {

    private final SpringDataPlayerContractRepository repository;

    public JpaPlayerContractRepositoryAdapter(SpringDataPlayerContractRepository repository) {
        this.repository = repository;
    }

    @Override
    public PlayerContract save(PlayerContract contract) {
        PlayerContractEntity entity = new PlayerContractEntity(
                contract.getId(),
                contract.getPlayerId(),
                contract.getTeamId(),
                contract.getFechaInicio(),
                contract.getFechaFin(),
                contract.getNumeroCamiseta(),
                contract.getEstado(),
                contract.getObservaciones(),
                contract.getCreadoEn(),
                contract.getActualizadoEn()
        );
        PlayerContractEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PlayerContract> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<PlayerContract> findActiveContractByPlayerId(UUID playerId) {
        return repository.findByPlayerIdAndEstado(playerId, ContractStatus.ACTIVO).map(this::toDomain);
    }

    @Override
    public List<PlayerContract> findByPlayerIdOrderByFechaInicioDesc(UUID playerId) {
        return repository.findByPlayerIdOrderByFechaInicioDesc(playerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PlayerContract> findActiveContractsByTeamId(UUID teamId) {
        return repository.findByTeamIdAndEstado(teamId, ContractStatus.ACTIVO).stream().map(this::toDomain).toList();
    }

    @Override
    public List<PlayerContract> findAllByTeamId(UUID teamId) {
        return repository.findByTeamId(teamId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsActiveContractByPlayerIdAndTeamId(UUID playerId, UUID teamId) {
        return repository.existsByPlayerIdAndTeamIdAndEstado(playerId, teamId, ContractStatus.ACTIVO);
    }

    private PlayerContract toDomain(PlayerContractEntity entity) {
        return new PlayerContract(
                entity.getId(),
                entity.getPlayerId(),
                entity.getTeamId(),
                entity.getFechaInicio(),
                entity.getFechaFin(),
                entity.getNumeroCamiseta(),
                entity.getEstado(),
                entity.getObservaciones(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }
}
