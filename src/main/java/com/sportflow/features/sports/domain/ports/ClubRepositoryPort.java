package com.sportflow.features.sports.domain.ports;

import com.sportflow.features.sports.domain.model.Club;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClubRepositoryPort {
    Club save(Club club);
    Optional<Club> findById(UUID id);
    List<Club> findAll();
    void deleteById(UUID id);
}
