package com.sportflow.features.sports.infrastructure.persistence.adapters;

import com.sportflow.features.sports.domain.model.Club;
import com.sportflow.features.sports.domain.ports.ClubRepositoryPort;
import com.sportflow.features.sports.infrastructure.persistence.entities.ClubEntity;
import com.sportflow.features.sports.infrastructure.persistence.repositories.SpringDataClubRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaClubRepositoryAdapter implements ClubRepositoryPort {

    private final SpringDataClubRepository repository;

    public JpaClubRepositoryAdapter(SpringDataClubRepository repository) {
        this.repository = repository;
    }

    @Override
    public Club save(Club club) {
        ClubEntity entity = new ClubEntity(
                club.getId(),
                club.getNombre(),
                club.getCiudad(),
                club.getDatosContacto(),
                club.isActivo(),
                club.getCreadoEn(),
                club.getActualizadoEn()
        );
        ClubEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Club> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Club> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Club toDomain(ClubEntity entity) {
        return new Club(
                entity.getId(),
                entity.getNombre(),
                entity.getCiudad(),
                entity.getDatosContacto(),
                entity.isActivo(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }
}
