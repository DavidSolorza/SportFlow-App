package com.sportflow.features.competitions.infrastructure.persistence.adapters;

import com.sportflow.features.competitions.domain.model.RegistrationStatus;
import com.sportflow.features.competitions.domain.model.TournamentRegistration;
import com.sportflow.features.competitions.domain.ports.TournamentRegistrationRepositoryPort;
import com.sportflow.features.competitions.infrastructure.persistence.entities.TournamentRegistrationEntity;
import com.sportflow.features.competitions.infrastructure.persistence.repositories.SpringDataTournamentRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaTournamentRegistrationRepositoryAdapter implements TournamentRegistrationRepositoryPort {

    private final SpringDataTournamentRegistrationRepository repository;

    public JpaTournamentRegistrationRepositoryAdapter(SpringDataTournamentRegistrationRepository repository) {
        this.repository = repository;
    }

    @Override
    public TournamentRegistration save(TournamentRegistration registration) {
        TournamentRegistrationEntity entity = new TournamentRegistrationEntity(
                registration.getId(),
                registration.getTournamentId(),
                registration.getTeamId(),
                registration.getFechaInscripcion(),
                registration.getEstado(),
                registration.getObservaciones(),
                registration.getCreadoEn()
        );
        TournamentRegistrationEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TournamentRegistration> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<TournamentRegistration> findByTournamentIdAndTeamId(UUID tournamentId, UUID teamId) {
        return repository.findByTournamentIdAndTeamId(tournamentId, teamId).map(this::toDomain);
    }

    @Override
    public List<TournamentRegistration> findByTournamentId(UUID tournamentId) {
        return repository.findByTournamentId(tournamentId).stream().map(this::toDomain).toList();
    }

    @Override
    public int countAcceptedByTournamentId(UUID tournamentId) {
        return repository.countByTournamentIdAndEstado(tournamentId, RegistrationStatus.ACEPTADA);
    }

    @Override
    public boolean existsByTournamentIdAndTeamId(UUID tournamentId, UUID teamId) {
        return repository.existsByTournamentIdAndTeamId(tournamentId, teamId);
    }

    private TournamentRegistration toDomain(TournamentRegistrationEntity entity) {
        return new TournamentRegistration(
                entity.getId(),
                entity.getTournamentId(),
                entity.getTeamId(),
                entity.getFechaInscripcion(),
                entity.getEstado(),
                entity.getObservaciones(),
                entity.getCreadoEn()
        );
    }
}
