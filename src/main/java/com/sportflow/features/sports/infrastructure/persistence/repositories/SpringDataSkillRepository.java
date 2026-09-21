package com.sportflow.features.sports.infrastructure.persistence.repositories;

import com.sportflow.features.sports.infrastructure.persistence.entities.SkillEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataSkillRepository extends JpaRepository<SkillEntity, UUID> {
    Optional<SkillEntity> findByNombreCanonico(String nombreCanonico);
    List<SkillEntity> findByActivo(boolean activo);
}
