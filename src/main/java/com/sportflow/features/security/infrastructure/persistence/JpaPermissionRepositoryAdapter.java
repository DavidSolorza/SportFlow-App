package com.sportflow.features.security.infrastructure.persistence;

import com.sportflow.features.security.domain.model.Permission;
import com.sportflow.features.security.domain.model.PermissionOperation;
import com.sportflow.features.security.domain.ports.PermissionRepositoryPort;
import com.sportflow.features.security.infrastructure.persistence.repositories.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class JpaPermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final SpringDataPermisoRepository permisoRepository;

    public JpaPermissionRepositoryAdapter(SpringDataPermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return permisoRepository.findById(id).map(SecurityEntityMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByRecursoAndOperacion(String recurso, PermissionOperation operacion) {
        return permisoRepository.findByRecursoAndOperacion(recurso, operacion.name())
                .map(SecurityEntityMapper::toDomain);
    }

    @Override
    public Permission save(Permission permission) {
        PermisoJpaEntity entity = SecurityEntityMapper.toEntity(permission);
        PermisoJpaEntity saved = permisoRepository.save(entity);
        return SecurityEntityMapper.toDomain(saved);
    }

    @Override
    public List<Permission> findAll() {
        return permisoRepository.findAll().stream()
                .map(SecurityEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findByIds(Set<UUID> ids) {
        return permisoRepository.findAllById(ids).stream()
                .map(SecurityEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        permisoRepository.deleteById(id);
    }
}
