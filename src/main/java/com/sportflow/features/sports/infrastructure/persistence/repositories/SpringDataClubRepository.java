package com.sportflow.features.sports.infrastructure.persistence.repositories;

import com.sportflow.features.sports.infrastructure.persistence.entities.ClubEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataClubRepository extends JpaRepository<ClubEntity, UUID> {
}
