package com.sportflow.features.competitions.infrastructure.persistence.adapters;

import com.sportflow.features.competitions.domain.model.MatchResult;
import com.sportflow.features.competitions.domain.ports.MatchResultRepositoryPort;
import com.sportflow.features.competitions.infrastructure.persistence.entities.MatchResultEntity;
import com.sportflow.features.competitions.infrastructure.persistence.repositories.SpringDataMatchResultRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class JpaMatchResultRepositoryAdapter implements MatchResultRepositoryPort {

    private final SpringDataMatchResultRepository repository;

    public JpaMatchResultRepositoryAdapter(SpringDataMatchResultRepository repository) {
        this.repository = repository;
    }

    @Override
    public MatchResult save(MatchResult result) {
        MatchResultEntity entity = new MatchResultEntity(
                result.getId(),
                result.getMatchId(),
                result.getGolesLocal(),
                result.getGolesVisitante(),
                result.getFechaConfirmacion(),
                result.getConfirmadoPor(),
                result.getObservaciones()
        );
        MatchResultEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MatchResult> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<MatchResult> findByMatchId(UUID matchId) {
        return repository.findByMatchId(matchId).map(this::toDomain);
    }

    @Override
    public void deleteByMatchId(UUID matchId) {
        repository.deleteByMatchId(matchId);
    }

    private MatchResult toDomain(MatchResultEntity entity) {
        return new MatchResult(
                entity.getId(),
                entity.getMatchId(),
                entity.getGolesLocal(),
                entity.getGolesVisitante(),
                entity.getFechaConfirmacion(),
                entity.getConfirmadoPor(),
                entity.getObservaciones()
        );
    }
}
