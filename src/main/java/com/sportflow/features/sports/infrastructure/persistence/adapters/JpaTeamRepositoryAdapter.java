package com.sportflow.features.sports.infrastructure.persistence.adapters;

import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import com.sportflow.features.sports.infrastructure.persistence.entities.TeamEntity;
import com.sportflow.features.sports.infrastructure.persistence.repositories.SpringDataTeamRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaTeamRepositoryAdapter implements TeamRepositoryPort {

    private final SpringDataTeamRepository repository;

    public JpaTeamRepositoryAdapter(SpringDataTeamRepository repository) {
        this.repository = repository;
    }

    @Override
    public Team save(Team team) {
        TeamEntity entity = new TeamEntity(
                team.getId(),
                team.getClubId(),
                team.getNombreDistintivo(),
                team.getCiudad(),
                team.getCategoria(),
                team.getGenero(),
                team.getEstado(),
                team.getFechaInscripcion(),
                team.getDeporteIds(),
                team.getCreadoEn(),
                team.getActualizadoEn()
        );
        TeamEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Team> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Team> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Team> findByClubId(UUID clubId) {
        return repository.findByClubId(clubId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Team> findByDeporteId(UUID deporteId) {
        return repository.findByDeporteId(deporteId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByDeporteId(UUID deporteId) {
        return repository.existsByDeporteId(deporteId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Team toDomain(TeamEntity entity) {
        return new Team(
                entity.getId(),
                entity.getClubId(),
                entity.getNombreDistintivo(),
                entity.getCiudad(),
                entity.getCategoria(),
                entity.getGenero(),
                entity.getEstado(),
                entity.getFechaInscripcion(),
                new HashSet<>(entity.getDeporteIds()),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }
}
