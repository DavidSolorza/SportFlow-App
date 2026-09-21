package com.sportflow.features.security.infrastructure.controllers;

import com.sportflow.features.security.application.dto.ManagementDTOs.*;
import com.sportflow.features.security.application.usecase.ManageUsersUseCase;
import com.sportflow.features.security.application.usecase.ToggleTwoFactorUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final ManageUsersUseCase manageUsersUseCase;
    private final ToggleTwoFactorUseCase toggleTwoFactorUseCase;

    public UserController(ManageUsersUseCase manageUsersUseCase, ToggleTwoFactorUseCase toggleTwoFactorUseCase) {
        this.manageUsersUseCase = manageUsersUseCase;
        this.toggleTwoFactorUseCase = toggleTwoFactorUseCase;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<UserListItemResponse>> listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<UserListItemResponse> response = manageUsersUseCase.listUsers(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailResponse> getUserDetail(@PathVariable("id") UUID id) {
        UserDetailResponse response = manageUsersUseCase.getUserDetail(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable("id") UUID id,
            @RequestBody UpdateProfileCommand command) {
        UserProfileDTO updatedProfile = manageUsersUseCase.updateProfile(id, command);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Perfil actualizado exitosamente.",
                "usuarioId", id,
                "perfil", updatedProfile
        ));
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<Map<String, Object>> assignRoles(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AssignRolesCommand command) {
        Set<String> rolesActuales = manageUsersUseCase.assignRoles(id, command);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Roles asignados exitosamente al usuario.",
                "usuarioId", id,
                "rolesActuales", rolesActuales
        ));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> toggleStatus(
            @PathVariable("id") UUID id,
            @RequestParam boolean activar) {
        manageUsersUseCase.toggleUserStatus(id, activar);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Estado del usuario actualizado exitosamente.",
                "usuarioId", id,
                "activo", activar
        ));
    }

    @PatchMapping("/{id}/2fa")
    public ResponseEntity<Map<String, Object>> toggle2FA(
            @PathVariable("id") UUID id,
            @RequestParam boolean habilitar) {
        boolean estadoActual = toggleTwoFactorUseCase.execute(id, habilitar);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Autenticación de dos factores " + (estadoActual ? "habilitada" : "deshabilitada") + " exitosamente.",
                "usuarioId", id,
                "dosFactoresHabilitado", estadoActual
        ));
    }
}
