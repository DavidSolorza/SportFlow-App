package com.sportflow.features.security.infrastructure.persistence;

import com.sportflow.features.security.domain.model.Role;
import com.sportflow.features.security.domain.ports.RoleRepositoryPort;
import com.sportflow.features.security.infrastructure.persistence.repositories.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaRoleRepositoryAdapter implements RoleRepositoryPort {

    private final SpringDataRolRepository rolRepository;
    private final SpringDataUsuarioRepository usuarioRepository;

    public JpaRoleRepositoryAdapter(SpringDataRolRepository rolRepository,
                                   SpringDataUsuarioRepository usuarioRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return rolRepository.findById(id).map(SecurityEntityMapper::toDomain);
    }

    @Override
    public Optional<Role> findByNombre(String nombre) {
        return rolRepository.findByNombre(nombre).map(SecurityEntityMapper::toDomain);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return rolRepository.existsByNombre(nombre);
    }

    @Override
    public Role save(Role role) {
        RolJpaEntity entity = SecurityEntityMapper.toEntity(role);
        RolJpaEntity saved = rolRepository.save(entity);
        return SecurityEntityMapper.toDomain(saved);
    }

    @Override
    public List<Role> findAll() {
        return rolRepository.findAll().stream()
                .map(SecurityEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        rolRepository.deleteById(id);
    }

    @Override
    public boolean hasAssignedUsers(UUID roleId) {
        return usuarioRepository.existsUserWithRole(roleId);
    }
}
