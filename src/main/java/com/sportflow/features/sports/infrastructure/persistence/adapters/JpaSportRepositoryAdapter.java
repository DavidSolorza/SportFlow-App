package com.sportflow.features.sports.infrastructure.persistence.adapters;

import com.sportflow.features.sports.domain.model.Sport;
import com.sportflow.features.sports.domain.ports.SportRepositoryPort;
import com.sportflow.features.sports.infrastructure.persistence.entities.SportEntity;
import com.sportflow.features.sports.infrastructure.persistence.repositories.SpringDataSportRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaSportRepositoryAdapter implements SportRepositoryPort {

    private final SpringDataSportRepository repository;

    public JpaSportRepositoryAdapter(SpringDataSportRepository repository) {
        this.repository = repository;
    }

    @Override
    public Sport save(Sport sport) {
        SportEntity entity = new SportEntity(
                sport.getId(),
                sport.getNombreCanonico(),
                sport.getDescripcion(),
                sport.isActivo(),
                sport.getDeportePadreId(),
                sport.getCreadoEn(),
                sport.getActualizadoEn()
        );
        SportEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Sport> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Sport> findByNombreCanonico(String nombreCanonico) {
        return repository.findByNombreCanonico(nombreCanonico).map(this::toDomain);
    }

    @Override
    public List<Sport> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Sport> findByActivo(boolean activo) {
        return repository.findByActivo(activo).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Sport> findByDeportePadreId(UUID deportePadreId) {
        return repository.findByDeportePadreId(deportePadreId).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsByDeportePadreId(UUID deportePadreId) {
        return repository.existsByDeportePadreId(deportePadreId);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Sport toDomain(SportEntity entity) {
        return new Sport(
                entity.getId(),
                entity.getNombreCanonico(),
                entity.getDescripcion(),
                entity.isActivo(),
                entity.getDeportePadreId(),
                new ArrayList<>(),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }
}
