package com.sportflow.features.sports.infrastructure.persistence.repositories;

import com.sportflow.features.sports.infrastructure.persistence.entities.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataPlayerRepository extends JpaRepository<PlayerEntity, UUID> {
    Optional<PlayerEntity> findByTipoDocumentoAndNumeroIdentificacion(String tipoDocumento, String numeroIdentificacion);
}
