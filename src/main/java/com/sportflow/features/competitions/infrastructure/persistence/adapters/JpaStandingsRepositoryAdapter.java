package com.sportflow.features.competitions.infrastructure.persistence.adapters;

import com.sportflow.features.competitions.domain.model.StandingsEntry;
import com.sportflow.features.competitions.domain.ports.StandingsRepositoryPort;
import com.sportflow.features.competitions.infrastructure.persistence.entities.StandingsEntity;
import com.sportflow.features.competitions.infrastructure.persistence.repositories.SpringDataStandingsRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaStandingsRepositoryAdapter implements StandingsRepositoryPort {

    private final SpringDataStandingsRepository repository;

    public JpaStandingsRepositoryAdapter(SpringDataStandingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public StandingsEntry save(StandingsEntry entry) {
        StandingsEntity saved = repository.save(toEntity(entry));
        return toDomain(saved);
    }

    @Override
    public List<StandingsEntry> saveAll(List<StandingsEntry> entries) {
        List<StandingsEntity> entities = entries.stream().map(this::toEntity).toList();
        return repository.saveAll(entities).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<StandingsEntry> findByPhaseIdAndGroupIdAndTeamId(UUID phaseId, UUID groupId, UUID teamId) {
        return repository.findByPhaseIdAndGroupIdAndTeamId(phaseId, groupId, teamId).map(this::toDomain);
    }

    @Override
    public List<StandingsEntry> findByPhaseIdAndGroupIdOrderByPuntosDescDiferenciaGolesDescGolesFavorDesc(UUID phaseId, UUID groupId) {
        return repository.findByPhaseIdAndGroupIdOrderByPuntosDescDiferenciaGolesDescGolesFavorDesc(phaseId, groupId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<StandingsEntry> findByPhaseIdOrderByPuntosDescDiferenciaGolesDescGolesFavorDesc(UUID phaseId) {
        return repository.findByPhaseIdOrderByPuntosDescDiferenciaGolesDescGolesFavorDesc(phaseId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteByPhaseId(UUID phaseId) {
        repository.deleteByPhaseId(phaseId);
    }

    private StandingsEntity toEntity(StandingsEntry entry) {
        return new StandingsEntity(
                entry.getId(),
                entry.getPhaseId(),
                entry.getGroupId(),
                entry.getTeamId(),
                entry.getPartidosJugados(),
                entry.getVictorias(),
                entry.getEmpates(),
                entry.getDerrotas(),
                entry.getGolesFavor(),
                entry.getGolesContra(),
                entry.getDiferenciaGoles(),
                entry.getPuntos(),
                entry.getPosicion(),
                entry.getFechaCalculo()
        );
    }

    private StandingsEntry toDomain(StandingsEntity entity) {
        return new StandingsEntry(
                entity.getId(),
                entity.getPhaseId(),
                entity.getGroupId(),
                entity.getTeamId(),
                entity.getPartidosJugados(),
                entity.getVictorias(),
                entity.getEmpates(),
                entity.getDerrotas(),
                entity.getGolesFavor(),
                entity.getGolesContra(),
                entity.getDiferenciaGoles(),
                entity.getPuntos(),
                entity.getPosicion(),
                entity.getFechaCalculo()
        );
    }
}
