package com.sportflow.features.security.application.usecase;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.ConflictException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.security.application.dto.ManagementDTOs.*;
import com.sportflow.features.security.domain.model.Permission;
import com.sportflow.features.security.domain.model.Role;
import com.sportflow.features.security.domain.ports.PermissionRepositoryPort;
import com.sportflow.features.security.domain.ports.RoleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageRolesUseCase {

    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;

    public ManageRolesUseCase(RoleRepositoryPort roleRepository, PermissionRepositoryPort permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse getRole(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("ROLE_NOT_FOUND", "Rol con ID " + roleId + " no existe."));
        return mapToResponse(role);
    }

    @Transactional
    public RoleResponse createRole(CreateRoleCommand command) {
        String nombreNormalizado = command.nombre().trim().toUpperCase();
        if (roleRepository.existsByNombre(nombreNormalizado)) {
            throw new ConflictException("ROLE_NAME_DUPLICATED", "Ya existe un rol registrado con el nombre '" + nombreNormalizado + "'.");
        }

        Role nuevoRol = Role.crear(nombreNormalizado, command.descripcion().trim());
        Role saved = roleRepository.save(nuevoRol);
        return mapToResponse(saved);
    }

    @Transactional
    public RoleResponse updateRole(UUID roleId, CreateRoleCommand command) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("ROLE_NOT_FOUND", "Rol no encontrado."));

        role.actualizar(command.descripcion(), role.isActivo());
        Role saved = roleRepository.save(role);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteRole(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("ROLE_NOT_FOUND", "Rol no encontrado."));

        // HU-SE-03: Validar si el rol se encuentra asignado a usuarios antes de eliminar
        if (roleRepository.hasAssignedUsers(roleId)) {
            throw new BusinessRuleException("ROLE_HAS_ASSIGNED_USERS",
                    "No se puede eliminar el rol '" + role.getNombre() + "' porque actualmente se encuentra asignado a uno o más usuarios.");
        }

        roleRepository.delete(roleId);
    }

    @Transactional
    public RoleResponse assignPermissionsToRole(UUID roleId, AssignPermissionsCommand command) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("ROLE_NOT_FOUND", "Rol no encontrado."));

        List<Permission> permissions = permissionRepository.findByIds(command.permisoIds());
        role.sincronizarPermisos(new HashSet<>(permissions));
        Role saved = roleRepository.save(role);
        return mapToResponse(saved);
    }

    private RoleResponse mapToResponse(Role role) {
        Set<PermissionResponse> permisosDTO = role.getPermisos().stream()
                .map(p -> new PermissionResponse(
                        p.getId(),
                        p.getRecurso(),
                        p.getOperacion().name(),
                        p.getMetodoHttp(),
                        p.getRutaUrl(),
                        p.getDescripcion()
                ))
                .collect(Collectors.toSet());

        return new RoleResponse(
                role.getId(),
                role.getNombre(),
                role.getDescripcion(),
                role.isActivo(),
                permisosDTO
        );
    }
}
