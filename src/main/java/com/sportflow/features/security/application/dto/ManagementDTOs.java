package com.sportflow.features.security.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class ManagementDTOs {
    private ManagementDTOs() {}

    public record UpdateProfileCommand(
            String telefono,
            String fotoUrl,
            String biografia
    ) {}

    public record CreateRoleCommand(
            @NotBlank(message = "El nombre del rol es obligatorio")
            String nombre,

            @NotBlank(message = "La descripción es obligatoria")
            String descripcion
    ) {}

    public record AssignRolesCommand(
            @NotEmpty(message = "Debe especificar al menos un identificador de rol")
            Set<UUID> rolIds,

            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {}

    public record AssignPermissionsCommand(
            @NotEmpty(message = "Debe especificar al menos un identificador de permiso")
            Set<UUID> permisoIds
    ) {}

    public record UserProfileDTO(
            String telefono,
            String fotoUrl,
            String biografia
    ) {}

    public record UserRoleDTO(
            UUID id,
            String nombre,
            String descripcion
    ) {}

    public record UserDetailResponse(
            UUID id,
            UUID personaId,
            String tipoDocumento,
            String numeroDocumento,
            String nombres,
            String apellidos,
            String email,
            String telefono,
            String nombreUsuario,
            String estado,
            String proveedorAuth,
            boolean dosFactoresHabilitado,
            UserProfileDTO perfil,
            List<UserRoleDTO> roles
    ) {}

    public record UserListItemResponse(
            UUID id,
            String nombreUsuario,
            String email,
            String nombres,
            String apellidos,
            String estado,
            String proveedorAuth,
            boolean dosFactoresHabilitado,
            Set<String> roles
    ) {}

    public record RoleResponse(
            UUID id,
            String nombre,
            String descripcion,
            boolean activo,
            Set<PermissionResponse> permisos
    ) {}

    public record PermissionResponse(
            UUID id,
            String recurso,
            String operacion,
            String metodoHttp,
            String rutaUrl,
            String descripcion
    ) {}

    public record PagedResponse<T>(
            List<T> contenido,
            long totalElementos,
            int totalPaginas,
            int paginaActual
    ) {}
}
