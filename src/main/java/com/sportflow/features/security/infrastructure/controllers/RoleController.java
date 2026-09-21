package com.sportflow.features.security.infrastructure.controllers;

import com.sportflow.features.security.application.dto.ManagementDTOs.*;
import com.sportflow.features.security.application.usecase.ManageRolesUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final ManageRolesUseCase manageRolesUseCase;

    public RoleController(ManageRolesUseCase manageRolesUseCase) {
        this.manageRolesUseCase = manageRolesUseCase;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> listRoles() {
        return ResponseEntity.ok(manageRolesUseCase.listRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(manageRolesUseCase.getRole(id));
    }

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleCommand command) {
        RoleResponse response = manageRolesUseCase.createRole(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable("id") UUID id, @Valid @RequestBody CreateRoleCommand command) {
        RoleResponse response = manageRolesUseCase.updateRole(id, command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") UUID id) {
        manageRolesUseCase.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/permissions")
    public ResponseEntity<Map<String, Object>> assignPermissions(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AssignPermissionsCommand command) {
        RoleResponse role = manageRolesUseCase.assignPermissionsToRole(id, command);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Matriz de permisos actualizada exitosamente para el rol.",
                "rolId", role.id(),
                "permisosAsignadosTotal", role.permisos().size()
        ));
    }
}
