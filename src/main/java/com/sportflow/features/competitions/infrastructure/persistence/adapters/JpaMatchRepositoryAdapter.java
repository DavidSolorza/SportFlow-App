package com.sportflow.features.competitions.infrastructure.persistence.adapters;

import com.sportflow.features.competitions.domain.model.Match;
import com.sportflow.features.competitions.domain.model.MatchStatus;
import com.sportflow.features.competitions.domain.ports.MatchRepositoryPort;
import com.sportflow.features.competitions.infrastructure.persistence.entities.MatchEntity;
import com.sportflow.features.competitions.infrastructure.persistence.repositories.SpringDataMatchRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaMatchRepositoryAdapter implements MatchRepositoryPort {

    private final SpringDataMatchRepository repository;

    public JpaMatchRepositoryAdapter(SpringDataMatchRepository repository) {
        this.repository = repository;
    }

    @Override
    public Match save(Match match) {
        MatchEntity entity = new MatchEntity(
                match.getId(),
                match.getPhaseId(),
                match.getGroupId(),
                match.getEquipoLocalId(),
                match.getEquipoVisitanteId(),
                match.getFechaHoraProgramada(),
                match.getEscenario(),
                match.getEstado(),
                match.isResultadoConfirmado(),
                match.getPartidoOrigen1(),
                match.getPartidoOrigen2(),
                match.getCreadoEn(),
                match.getActualizadoEn()
        );
        MatchEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Match> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Match> findByPhaseId(UUID phaseId) {
        return repository.findByPhaseId(phaseId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Match> findByGroupId(UUID groupId) {
        return repository.findByGroupId(groupId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Match> findByPhaseIdAndEstado(UUID phaseId, MatchStatus estado) {
        return repository.findByPhaseIdAndEstado(phaseId, estado).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Match> findByPartidoOrigen(UUID partidoId) {
        return repository.findByPartidoOrigen(partidoId).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Match toDomain(MatchEntity entity) {
        return new Match(
                entity.getId(),
                entity.getPhaseId(),
                entity.getGroupId(),
                entity.getEquipoLocalId(),
                entity.getEquipoVisitanteId(),
                entity.getFechaHoraProgramada(),
                entity.getEscenario(),
                entity.getEstado(),
                entity.isResultadoConfirmado(),
                entity.getPartidoOrigen1(),
                entity.getPartidoOrigen2(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }
}
