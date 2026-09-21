package com.sportflow.features.security.infrastructure.persistence.repositories;

import com.sportflow.features.security.infrastructure.persistence.RolJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataRolRepository extends JpaRepository<RolJpaEntity, UUID> {
    Optional<RolJpaEntity> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
