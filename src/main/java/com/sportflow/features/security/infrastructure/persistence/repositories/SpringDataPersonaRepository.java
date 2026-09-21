package com.sportflow.features.security.infrastructure.persistence.repositories;

import com.sportflow.features.security.infrastructure.persistence.PersonaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPersonaRepository extends JpaRepository<PersonaJpaEntity, UUID> {
    Optional<PersonaJpaEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
