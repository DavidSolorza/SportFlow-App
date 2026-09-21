package com.sportflow.features.security.domain.ports;

import com.sportflow.features.security.domain.model.Permission;
import com.sportflow.features.security.domain.model.PermissionOperation;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PermissionRepositoryPort {
    Optional<Permission> findById(UUID id);
    Optional<Permission> findByRecursoAndOperacion(String recurso, PermissionOperation operacion);
    Permission save(Permission permission);
    List<Permission> findAll();
    List<Permission> findByIds(Set<UUID> ids);
    void delete(UUID id);
}
