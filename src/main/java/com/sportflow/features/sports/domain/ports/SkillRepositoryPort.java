package com.sportflow.features.sports.domain.ports;

import com.sportflow.features.sports.domain.model.Skill;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillRepositoryPort {
    Skill save(Skill skill);
    Optional<Skill> findById(UUID id);
    Optional<Skill> findByNombreCanonico(String nombreCanonico);
    List<Skill> findAll();
    List<Skill> findByActivo(boolean activo);
    void deleteById(UUID id);
}
