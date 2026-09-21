package com.sportflow.features.security.infrastructure.controllers;

import com.sportflow.features.security.application.dto.ManagementDTOs.PermissionResponse;
import com.sportflow.features.security.application.usecase.ManagePermissionsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {

    private final ManagePermissionsUseCase managePermissionsUseCase;

    public PermissionController(ManagePermissionsUseCase managePermissionsUseCase) {
        this.managePermissionsUseCase = managePermissionsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> listAll() {
        return ResponseEntity.ok(managePermissionsUseCase.listPermissions());
    }

    @GetMapping("/by-resource")
    public ResponseEntity<Map<String, List<PermissionResponse>>> listGroupedByResource() {
        return ResponseEntity.ok(managePermissionsUseCase.listPermissionsGroupedByResource());
    }
}
