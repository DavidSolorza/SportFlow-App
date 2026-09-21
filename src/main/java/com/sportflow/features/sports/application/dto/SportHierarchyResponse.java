package com.sportflow.features.sports.application.dto;

import java.util.List;
import java.util.UUID;

public record SportHierarchyResponse(
        UUID id,
        String nombreCanonico,
        String descripcion,
        boolean activo,
        UUID deportePadreId,
        List<SportHierarchyResponse> subdeportes
) {}
