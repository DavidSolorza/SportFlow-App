package com.sportflow.features.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportflow.features.security.application.dto.LoginCommand;
import com.sportflow.features.security.application.dto.ManagementDTOs.CreateRoleCommand;
import com.sportflow.features.security.application.dto.PasswordResetDTOs;
import com.sportflow.features.security.application.dto.RegisterUserCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class RegisterAndAuthenticateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("HU-SE-07: Registro exitoso de usuario con credenciales")
    void testRegisterUserSuccess() throws Exception {
        RegisterUserCommand command = new RegisterUserCommand(
                "CC", "11223344", "Juan", "Pérez",
                "juan.perez@sportflow.com", "+573100000000", "jperez", "Secret123!#"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("juan.perez@sportflow.com"))
                .andExpect(jsonPath("$.nombreUsuario").value("jperez"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @DisplayName("HU-SE-07: Rechazo de registro cuando el correo ya existe")
    void testRegisterUserDuplicateEmail() throws Exception {
        RegisterUserCommand command = new RegisterUserCommand(
                "CC", "99887766", "Admin", "Clone",
                "admin@sportflow.com", "+573100000000", "adminclone", "Secret123!#"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_REGISTERED"));
    }

    @Test
    @DisplayName("HU-SE-08: Inicio de sesión exitoso con credenciales SuperAdmin")
    void testLoginSuccess() throws Exception {
        LoginCommand login = new LoginCommand("admin@sportflow.com", "AdminPassword123!#");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("AUTENTICADO"))
                .andExpect(jsonPath("$.tokenAcceso").isString())
                .andExpect(jsonPath("$.usuario.email").value("admin@sportflow.com"));
    }

    @Test
    @DisplayName("HU-SE-08: Rechazo de login con contraseña incorrecta")
    void testLoginInvalidCredentials() throws Exception {
        LoginCommand login = new LoginCommand("admin@sportflow.com", "WrongPassword999!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("HU-SE-09: Solicitud de restablecimiento de contraseña")
    void testPasswordResetRequest() throws Exception {
        PasswordResetDTOs.RequestResetCommand resetCommand = new PasswordResetDTOs.RequestResetCommand("admin@sportflow.com");

        mockMvc.perform(post("/api/v1/auth/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @DisplayName("HU-SE-03: Restricción al eliminar rol si ya tiene usuarios asignados")
    void testDeleteRoleWithUsersFails() throws Exception {
        // Obtenemos el token de admin primero
        LoginCommand login = new LoginCommand("admin@sportflow.com", "AdminPassword123!#");
        String loginRes = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginRes).get("tokenAcceso").asText();

        // Consultamos los roles
        String rolesRes = mockMvc.perform(get("/api/v1/roles")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String adminRoleId = objectMapper.readTree(rolesRes).get(0).get("id").asText();

        // Intentar eliminar el rol ADMIN (que tiene asignado al superadmin)
        mockMvc.perform(delete("/api/v1/roles/" + adminRoleId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ROLE_HAS_ASSIGNED_USERS"));
    }
}
