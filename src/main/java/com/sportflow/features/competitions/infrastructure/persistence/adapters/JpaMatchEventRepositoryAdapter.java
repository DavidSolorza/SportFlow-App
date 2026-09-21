package com.sportflow.features.competitions.infrastructure.persistence.adapters;

import com.sportflow.features.competitions.domain.model.MatchEvent;
import com.sportflow.features.competitions.domain.ports.MatchEventRepositoryPort;
import com.sportflow.features.competitions.infrastructure.persistence.entities.MatchEventEntity;
import com.sportflow.features.competitions.infrastructure.persistence.repositories.SpringDataMatchEventRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaMatchEventRepositoryAdapter implements MatchEventRepositoryPort {

    private final SpringDataMatchEventRepository repository;

    public JpaMatchEventRepositoryAdapter(SpringDataMatchEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public MatchEvent save(MatchEvent event) {
        MatchEventEntity entity = new MatchEventEntity(
                event.getId(),
                event.getMatchId(),
                event.getJugadorId(),
                event.getEquipoId(),
                event.getTipoEvento(),
                event.getMinuto(),
                event.getDescripcion(),
                event.getEstadoValidacion(),
                event.getCreadoEn()
        );
        MatchEventEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MatchEvent> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<MatchEvent> findByMatchIdOrderByMinutoAsc(UUID matchId) {
        return repository.findByMatchIdOrderByMinutoAsc(matchId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private MatchEvent toDomain(MatchEventEntity entity) {
        return new MatchEvent(
                entity.getId(),
                entity.getMatchId(),
                entity.getJugadorId(),
                entity.getEquipoId(),
                entity.getTipoEvento(),
                entity.getMinuto(),
                entity.getDescripcion(),
                entity.getEstadoValidacion(),
                entity.getCreadoEn()
        );
    }
}
