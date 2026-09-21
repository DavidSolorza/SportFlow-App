package com.sportflow.features.sports.infrastructure.persistence.repositories;

import com.sportflow.features.sports.domain.model.ContractStatus;
import com.sportflow.features.sports.infrastructure.persistence.entities.PlayerContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPlayerContractRepository extends JpaRepository<PlayerContractEntity, UUID> {
    Optional<PlayerContractEntity> findByPlayerIdAndEstado(UUID playerId, ContractStatus estado);
    List<PlayerContractEntity> findByPlayerIdOrderByFechaInicioDesc(UUID playerId);
    List<PlayerContractEntity> findByTeamIdAndEstado(UUID teamId, ContractStatus estado);
    List<PlayerContractEntity> findByTeamId(UUID teamId);
    boolean existsByPlayerIdAndTeamIdAndEstado(UUID playerId, UUID teamId, ContractStatus estado);
}
