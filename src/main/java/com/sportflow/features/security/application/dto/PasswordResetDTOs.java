package com.sportflow.features.security.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class PasswordResetDTOs {
    private PasswordResetDTOs() {}

    public record RequestResetCommand(
            @NotBlank(message = "El correo electrónico es obligatorio")
            @Email(message = "El formato de correo es inválido")
            String email
    ) {}

    public record ConfirmResetCommand(
            @NotBlank(message = "El token de recuperación es obligatorio")
            String token,

            @NotBlank(message = "La nueva contraseña es obligatoria")
            @Size(min = 8, message = "La nueva contraseña debe tener mínimo 8 caracteres")
            String nuevaPassword
    ) {}

    public record SimpleMessageResponse(String mensaje) {}
}
