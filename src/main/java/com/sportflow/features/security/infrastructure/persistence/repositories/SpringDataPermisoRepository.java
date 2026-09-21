package com.sportflow.features.security.infrastructure.persistence.repositories;

import com.sportflow.features.security.infrastructure.persistence.PermisoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPermisoRepository extends JpaRepository<PermisoJpaEntity, UUID> {
    Optional<PermisoJpaEntity> findByRecursoAndOperacion(String recurso, String operacion);
}
