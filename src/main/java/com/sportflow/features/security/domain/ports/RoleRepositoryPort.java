package com.sportflow.features.security.domain.ports;

import com.sportflow.features.security.domain.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepositoryPort {
    Optional<Role> findById(UUID id);
    Optional<Role> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    Role save(Role role);
    List<Role> findAll();
    void delete(UUID id);
    boolean hasAssignedUsers(UUID roleId);
}
