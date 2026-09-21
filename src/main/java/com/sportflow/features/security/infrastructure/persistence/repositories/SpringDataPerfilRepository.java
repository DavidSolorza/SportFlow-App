package com.sportflow.features.security.infrastructure.persistence.repositories;

import com.sportflow.features.security.infrastructure.persistence.PerfilJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPerfilRepository extends JpaRepository<PerfilJpaEntity, UUID> {
    Optional<PerfilJpaEntity> findByUsuarioId(UUID usuarioId);
}
