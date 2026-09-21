package com.sportflow.features.security.infrastructure.persistence.repositories;

import com.sportflow.features.security.infrastructure.persistence.PasswordResetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPasswordResetRepository extends JpaRepository<PasswordResetJpaEntity, UUID> {
    Optional<PasswordResetJpaEntity> findByTokenHash(String tokenHash);
}
