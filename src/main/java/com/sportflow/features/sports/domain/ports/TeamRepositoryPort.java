package com.sportflow.features.sports.domain.ports;

import com.sportflow.features.sports.domain.model.Team;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepositoryPort {
    Team save(Team team);
    Optional<Team> findById(UUID id);
    List<Team> findAll();
    List<Team> findByClubId(UUID clubId);
    List<Team> findByDeporteId(UUID deporteId);
    boolean existsByDeporteId(UUID deporteId);
    void deleteById(UUID id);
}
