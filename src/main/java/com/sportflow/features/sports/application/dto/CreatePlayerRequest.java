package com.sportflow.features.sports.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CreatePlayerRequest(
        UUID personaId,
        @NotBlank(message = "El tipo de documento es obligatorio.")
        String tipoDocumento,
        @NotBlank(message = "El número de identificación es obligatorio.")
        String numeroIdentificacion,
        @NotBlank(message = "Los nombres son obligatorios.")
        String nombres,
        @NotBlank(message = "Los apellidos son obligatorios.")
        String apellidos,
        @NotNull(message = "La fecha de nacimiento es obligatoria.")
        LocalDate fechaNacimiento,
        String posicion
) {}
