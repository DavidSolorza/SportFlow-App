package com.sportflow.features.security.application.usecase;

import com.sportflow.core.errors.ConflictException;
import com.sportflow.features.security.application.dto.ManagementDTOs.PermissionResponse;
import com.sportflow.features.security.domain.model.Permission;
import com.sportflow.features.security.domain.model.PermissionOperation;
import com.sportflow.features.security.domain.ports.PermissionRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ManagePermissionsUseCase {

    private final PermissionRepositoryPort permissionRepository;

    public ManagePermissionsUseCase(PermissionRepositoryPort permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> listPermissions() {
        return permissionRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, List<PermissionResponse>> listPermissionsGroupedByResource() {
        return permissionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.groupingBy(PermissionResponse::recurso));
    }

    @Transactional
    public PermissionResponse createPermission(String recurso, PermissionOperation operacion,
                                              String metodoHttp, String rutaUrl, String descripcion) {
        if (permissionRepository.findByRecursoAndOperacion(recurso, operacion).isPresent()) {
            throw new ConflictException("PERMISSION_DUPLICATED",
                    "Ya existe un permiso para el recurso '" + recurso + "' y operación '" + operacion + "'.");
        }

        Permission p = Permission.crear(recurso, operacion, metodoHttp, rutaUrl, descripcion);
        Permission saved = permissionRepository.save(p);
        return mapToResponse(saved);
    }

    private PermissionResponse mapToResponse(Permission p) {
        return new PermissionResponse(
                p.getId(),
                p.getRecurso(),
                p.getOperacion().name(),
                p.getMetodoHttp(),
                p.getRutaUrl(),
                p.getDescripcion()
        );
    }
}
