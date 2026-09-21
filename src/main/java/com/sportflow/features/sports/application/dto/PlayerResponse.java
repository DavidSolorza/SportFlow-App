package com.sportflow.features.sports.application.dto;

import com.sportflow.features.sports.domain.model.PlayerStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PlayerResponse(
        UUID id,
        UUID personaId,
        String tipoDocumento,
        String numeroIdentificacion,
        String nombres,
        String apellidos,
        String nombreCompleto,
        LocalDate fechaNacimiento,
        String posicion,
        PlayerStatus estado
) {}
