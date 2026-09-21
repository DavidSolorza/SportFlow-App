package com.sportflow.features.security.application.dto;

import jakarta.validation.constraints.NotBlank;

public record OAuthLoginCommand(
        @NotBlank(message = "El token provisto por el proveedor es obligatorio")
        String tokenProveedor,

        String tipoToken
) {}
