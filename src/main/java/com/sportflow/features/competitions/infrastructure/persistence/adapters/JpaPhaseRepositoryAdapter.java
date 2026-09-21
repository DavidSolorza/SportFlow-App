package com.sportflow.features.competitions.infrastructure.persistence.adapters;

import com.sportflow.features.competitions.domain.model.Phase;
import com.sportflow.features.competitions.domain.ports.PhaseRepositoryPort;
import com.sportflow.features.competitions.infrastructure.persistence.entities.PhaseEntity;
import com.sportflow.features.competitions.infrastructure.persistence.repositories.SpringDataPhaseRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaPhaseRepositoryAdapter implements PhaseRepositoryPort {

    private final SpringDataPhaseRepository repository;

    public JpaPhaseRepositoryAdapter(SpringDataPhaseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Phase save(Phase phase) {
        PhaseEntity entity = new PhaseEntity(
                phase.getId(),
                phase.getTournamentId(),
                phase.getNombre(),
                phase.getTipo(),
                phase.getOrden(),
                phase.getEstado(),
                phase.getFasePadreId(),
                phase.getCreadoEn(),
                phase.getActualizadoEn()
        );
        PhaseEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Phase> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Phase> findByTournamentIdOrderByOrdenAsc(UUID tournamentId) {
        return repository.findByTournamentIdOrderByOrdenAsc(tournamentId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Phase> findByFasePadreId(UUID fasePadreId) {
        return repository.findByFasePadreId(fasePadreId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByTournamentId(UUID tournamentId) {
        return repository.existsByTournamentId(tournamentId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Phase toDomain(PhaseEntity entity) {
        return new Phase(
                entity.getId(),
                entity.getTournamentId(),
                entity.getNombre(),
                entity.getTipo(),
                entity.getOrden(),
                entity.getEstado(),
                entity.getFasePadreId(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }
}
