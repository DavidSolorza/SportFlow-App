package com.sportflow.features.sports.domain.ports;

import com.sportflow.features.sports.domain.model.Sport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SportRepositoryPort {
    Sport save(Sport sport);
    Optional<Sport> findById(UUID id);
    Optional<Sport> findByNombreCanonico(String nombreCanonico);
    List<Sport> findAll();
    List<Sport> findByActivo(boolean activo);
    List<Sport> findByDeportePadreId(UUID deportePadreId);
    boolean existsByDeportePadreId(UUID deportePadreId);
    void deleteById(UUID id);
}
