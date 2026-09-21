package com.sportflow.features.sports.infrastructure.persistence.repositories;

import com.sportflow.features.sports.infrastructure.persistence.entities.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataTeamRepository extends JpaRepository<TeamEntity, UUID> {
    List<TeamEntity> findByClubId(UUID clubId);

    @Query("SELECT t FROM TeamEntity t JOIN t.deporteIds d WHERE d = :deporteId")
    List<TeamEntity> findByDeporteId(@Param("deporteId") UUID deporteId);

    @Query("SELECT COUNT(t) > 0 FROM TeamEntity t JOIN t.deporteIds d WHERE d = :deporteId")
    boolean existsByDeporteId(@Param("deporteId") UUID deporteId);
}
