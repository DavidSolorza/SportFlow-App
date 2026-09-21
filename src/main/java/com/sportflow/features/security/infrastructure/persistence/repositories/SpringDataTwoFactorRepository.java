package com.sportflow.features.security.infrastructure.persistence.repositories;

import com.sportflow.features.security.infrastructure.persistence.TwoFactorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTwoFactorRepository extends JpaRepository<TwoFactorJpaEntity, UUID> {
    Optional<TwoFactorJpaEntity> findByDesafioToken(String desafioToken);
}
