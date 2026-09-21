package com.sportflow.features.competitions.infrastructure.persistence.adapters;

import com.sportflow.features.competitions.domain.model.Tournament;
import com.sportflow.features.competitions.domain.model.TournamentStatus;
import com.sportflow.features.competitions.domain.ports.TournamentRepositoryPort;
import com.sportflow.features.competitions.infrastructure.persistence.entities.TournamentEntity;
import com.sportflow.features.competitions.infrastructure.persistence.repositories.SpringDataTournamentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaTournamentRepositoryAdapter implements TournamentRepositoryPort {

    private final SpringDataTournamentRepository repository;

    public JpaTournamentRepositoryAdapter(SpringDataTournamentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tournament save(Tournament tournament) {
        TournamentEntity entity = new TournamentEntity(
                tournament.getId(),
                tournament.getSportId(),
                tournament.getNombre(),
                tournament.getDescripcion(),
                tournament.getFechaInicio(),
                tournament.getFechaFin(),
                tournament.getFechaCierreInscripcion(),
                tournament.getCupoEquipos(),
                tournament.getEstado(),
                tournament.getCreadoEn(),
                tournament.getActualizadoEn()
        );
        TournamentEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Tournament> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Tournament> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Tournament> findBySportId(UUID sportId) {
        return repository.findBySportId(sportId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Tournament> findByEstado(TournamentStatus estado) {
        return repository.findByEstado(estado).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Tournament toDomain(TournamentEntity entity) {
        return new Tournament(
                entity.getId(),
                entity.getSportId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.getFechaInicio(),
                entity.getFechaFin(),
                entity.getFechaCierreInscripcion(),
                entity.getCupoEquipos(),
                entity.getEstado(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }
}
