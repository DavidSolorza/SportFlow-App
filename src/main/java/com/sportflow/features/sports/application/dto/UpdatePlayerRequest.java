package com.sportflow.features.sports.application.dto;

import com.sportflow.features.sports.domain.model.PlayerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record UpdatePlayerRequest(
        @NotBlank(message = "Los nombres son obligatorios.")
        String nombres,
        @NotBlank(message = "Los apellidos son obligatorios.")
        String apellidos,
        @NotNull(message = "La fecha de nacimiento es obligatoria.")
        LocalDate fechaNacimiento,
        String posicion,
        PlayerStatus estado
) {}
