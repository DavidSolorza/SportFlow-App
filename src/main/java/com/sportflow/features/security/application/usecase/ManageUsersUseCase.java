package com.sportflow.features.security.application.usecase;

import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.security.application.dto.ManagementDTOs.*;
import com.sportflow.features.security.domain.model.Role;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.model.UserProfile;
import com.sportflow.features.security.domain.ports.RoleRepositoryPort;
import com.sportflow.features.security.domain.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageUsersUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;

    public ManageUsersUseCase(UserRepositoryPort userRepository, RoleRepositoryPort roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserListItemResponse> listUsers(int page, int size) {
        List<User> users = userRepository.findAll(page, size);
        long total = userRepository.count();

        List<UserListItemResponse> items = users.stream().map(u -> new UserListItemResponse(
                u.getId(),
                u.getNombreUsuario(),
                u.getEmail(),
                u.getPersona().getNombres(),
                u.getPersona().getApellidos(),
                u.getEstado().name(),
                u.getProveedorAuth().name(),
                u.isDosFactoresHabilitado(),
                u.obtenerNombresRoles()
        )).toList();

        int totalPages = (int) Math.ceil((double) total / size);
        return new PagedResponse<>(items, total, totalPages, page);
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(UUID userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND", "Usuario con ID " + userId + " no existe."));

        UserProfileDTO profileDTO = null;
        if (u.getPerfil() != null) {
            profileDTO = new UserProfileDTO(
                    u.getPerfil().getTelefono(),
                    u.getPerfil().getFotoUrl(),
                    u.getPerfil().getBiografia()
            );
        }

        List<UserRoleDTO> rolesDTO = u.getRoles().stream()
                .map(r -> new UserRoleDTO(r.getId(), r.getNombre(), r.getDescripcion()))
                .toList();

        return new UserDetailResponse(
                u.getId(),
                u.getPersona().getId(),
                u.getPersona().getTipoDocumento(),
                u.getPersona().getNumeroDocumento(),
                u.getPersona().getNombres(),
                u.getPersona().getApellidos(),
                u.getEmail(),
                u.getPersona().getTelefono(),
                u.getNombreUsuario(),
                u.getEstado().name(),
                u.getProveedorAuth().name(),
                u.isDosFactoresHabilitado(),
                profileDTO,
                rolesDTO
        );
    }

    @Transactional
    public UserProfileDTO updateProfile(UUID userId, UpdateProfileCommand command) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND", "Usuario no encontrado."));

        UserProfile profile = u.getPerfil();
        if (profile == null) {
            profile = UserProfile.inicializar(u.getId(), command.telefono());
            u.asignarPerfil(profile);
        }

        profile.actualizar(command.telefono(), command.fotoUrl(), command.biografia());
        userRepository.save(u);

        return new UserProfileDTO(profile.getTelefono(), profile.getFotoUrl(), profile.getBiografia());
    }

    @Transactional
    public Set<String> assignRoles(UUID userId, AssignRolesCommand command) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND", "Usuario no encontrado."));

        Set<Role> rolesToAssign = new HashSet<>();
        for (UUID roleId : command.rolIds()) {
            Role r = roleRepository.findById(roleId)
                    .orElseThrow(() -> new EntityNotFoundException("ROLE_NOT_FOUND", "Rol con ID " + roleId + " no existe."));
            rolesToAssign.add(r);
        }

        u.sincronizarRoles(rolesToAssign);
        userRepository.save(u);

        return u.obtenerNombresRoles();
    }

    @Transactional
    public void toggleUserStatus(UUID userId, boolean activar) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("USER_NOT_FOUND", "Usuario no encontrado."));

        if (activar) {
            u.activar();
        } else {
            u.desactivar();
        }

        userRepository.save(u);
    }
}
