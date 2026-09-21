package com.sportflow.features.security.infrastructure.persistence.repositories;

import com.sportflow.features.security.infrastructure.persistence.SesionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataSesionRepository extends JpaRepository<SesionJpaEntity, UUID> {
    Optional<SesionJpaEntity> findByTokenJti(String tokenJti);

    @Modifying
    @Query("UPDATE SesionJpaEntity s SET s.activa = false WHERE s.tokenJti = :jti")
    void revokeByTokenJti(@Param("jti") String jti);

    @Modifying
    @Query("UPDATE SesionJpaEntity s SET s.activa = false WHERE s.usuarioId = :userId")
    void revokeAllByUserId(@Param("userId") UUID userId);
}
