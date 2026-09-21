package com.sportflow.features.competitions.infrastructure.persistence.adapters;

import com.sportflow.features.competitions.domain.model.CompetitionGroup;
import com.sportflow.features.competitions.domain.ports.CompetitionGroupRepositoryPort;
import com.sportflow.features.competitions.infrastructure.persistence.entities.CompetitionGroupEntity;
import com.sportflow.features.competitions.infrastructure.persistence.repositories.SpringDataCompetitionGroupRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaCompetitionGroupRepositoryAdapter implements CompetitionGroupRepositoryPort {

    private final SpringDataCompetitionGroupRepository repository;

    public JpaCompetitionGroupRepositoryAdapter(SpringDataCompetitionGroupRepository repository) {
        this.repository = repository;
    }

    @Override
    public CompetitionGroup save(CompetitionGroup group) {
        CompetitionGroupEntity entity = new CompetitionGroupEntity(
                group.getId(),
                group.getPhaseId(),
                group.getNombre(),
                group.getOrden(),
                group.getEquipoIds(),
                group.getCreadoEn()
        );
        CompetitionGroupEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CompetitionGroup> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<CompetitionGroup> findByPhaseIdOrderByOrdenAsc(UUID phaseId) {
        return repository.findByPhaseIdOrderByOrdenAsc(phaseId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByPhaseIdAndEquipoId(UUID phaseId, UUID equipoId) {
        return repository.existsByPhaseIdAndEquipoId(phaseId, equipoId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private CompetitionGroup toDomain(CompetitionGroupEntity entity) {
        return new CompetitionGroup(
                entity.getId(),
                entity.getPhaseId(),
                entity.getNombre(),
                entity.getOrden(),
                new HashSet<>(entity.getEquipoIds()),
                entity.getCreadoEn()
        );
    }
}
