package com.sportflow.features.competitions.infrastructure.persistence.repositories;

import com.sportflow.features.competitions.infrastructure.persistence.entities.CompetitionGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataCompetitionGroupRepository extends JpaRepository<CompetitionGroupEntity, UUID> {
    List<CompetitionGroupEntity> findByPhaseIdOrderByOrdenAsc(UUID phaseId);

    @Query("SELECT COUNT(g) > 0 FROM CompetitionGroupEntity g JOIN g.equipoIds e WHERE g.phaseId = :phaseId AND e = :equipoId")
    boolean existsByPhaseIdAndEquipoId(@Param("phaseId") UUID phaseId, @Param("equipoId") UUID equipoId);
}
