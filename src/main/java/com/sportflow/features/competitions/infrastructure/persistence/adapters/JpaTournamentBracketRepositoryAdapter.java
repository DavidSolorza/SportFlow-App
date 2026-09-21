package com.sportflow.features.competitions.infrastructure.persistence.adapters;

import com.sportflow.features.competitions.domain.model.TournamentBracket;
import com.sportflow.features.competitions.domain.ports.TournamentBracketRepositoryPort;
import com.sportflow.features.competitions.infrastructure.persistence.entities.TournamentBracketEntity;
import com.sportflow.features.competitions.infrastructure.persistence.repositories.SpringDataTournamentBracketRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaTournamentBracketRepositoryAdapter implements TournamentBracketRepositoryPort {

    private final SpringDataTournamentBracketRepository repository;

    public JpaTournamentBracketRepositoryAdapter(SpringDataTournamentBracketRepository repository) {
        this.repository = repository;
    }

    @Override
    public TournamentBracket save(TournamentBracket bracket) {
        TournamentBracketEntity entity = new TournamentBracketEntity(
                bracket.getId(),
                bracket.getPhaseId(),
                bracket.getNombre(),
                bracket.getRonda(),
                bracket.getOrden(),
                bracket.getPartidoId(),
                bracket.getGanadorEquipoId(),
                bracket.getCreadoEn()
        );
        TournamentBracketEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TournamentBracket> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<TournamentBracket> findByPartidoId(UUID partidoId) {
        return repository.findByPartidoId(partidoId).map(this::toDomain);
    }

    @Override
    public List<TournamentBracket> findByPhaseIdOrderByRondaAscOrdenAsc(UUID phaseId) {
        return repository.findByPhaseIdOrderByRondaAscOrdenAsc(phaseId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private TournamentBracket toDomain(TournamentBracketEntity entity) {
        return new TournamentBracket(
                entity.getId(),
                entity.getPhaseId(),
                entity.getNombre(),
                entity.getRonda(),
                entity.getOrden(),
                entity.getPartidoId(),
                entity.getGanadorEquipoId(),
                entity.getCreadoEn()
        );
    }
}
