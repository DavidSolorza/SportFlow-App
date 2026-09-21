package com.sportflow.features.security.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record Verify2FACommand(
        @NotBlank(message = "El token de desafío es obligatorio")
        String desafioToken,

        @NotBlank(message = "El código 2FA es obligatorio")
        @Pattern(regexp = "^[0-9]{6}$", message = "El código debe ser de 6 dígitos numéricos")
        String codigo
) {}
