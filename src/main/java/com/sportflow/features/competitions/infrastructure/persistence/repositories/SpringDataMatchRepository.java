package com.sportflow.features.competitions.infrastructure.persistence.repositories;

import com.sportflow.features.competitions.domain.model.MatchStatus;
import com.sportflow.features.competitions.infrastructure.persistence.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataMatchRepository extends JpaRepository<MatchEntity, UUID> {
    List<MatchEntity> findByPhaseId(UUID phaseId);
    List<MatchEntity> findByGroupId(UUID groupId);
    List<MatchEntity> findByPhaseIdAndEstado(UUID phaseId, MatchStatus estado);

    @Query("SELECT m FROM MatchEntity m WHERE m.partidoOrigen1 = :partidoId OR m.partidoOrigen2 = :partidoId")
    List<MatchEntity> findByPartidoOrigen(@Param("partidoId") UUID partidoId);
}
