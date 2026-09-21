package com.sportflow.features.sports.infrastructure.persistence.repositories;

import com.sportflow.features.sports.infrastructure.persistence.entities.SportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataSportRepository extends JpaRepository<SportEntity, UUID> {
    Optional<SportEntity> findByNombreCanonico(String nombreCanonico);
    List<SportEntity> findByActivo(boolean activo);
    List<SportEntity> findByDeportePadreId(UUID deportePadreId);
    boolean existsByDeportePadreId(UUID deportePadreId);
}
