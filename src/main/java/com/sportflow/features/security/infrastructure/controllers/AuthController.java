package com.sportflow.features.security.infrastructure.controllers;

import com.sportflow.features.security.application.dto.*;
import com.sportflow.features.security.application.usecase.*;
import com.sportflow.features.security.domain.model.AuthProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateCredentialsUseCase authenticateCredentialsUseCase;
    private final AuthenticateOAuthUseCase authenticateOAuthUseCase;
    private final VerifyTwoFactorUseCase verifyTwoFactorUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ConfirmPasswordResetUseCase confirmPasswordResetUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          AuthenticateCredentialsUseCase authenticateCredentialsUseCase,
                          AuthenticateOAuthUseCase authenticateOAuthUseCase,
                          VerifyTwoFactorUseCase verifyTwoFactorUseCase,
                          RequestPasswordResetUseCase requestPasswordResetUseCase,
                          ConfirmPasswordResetUseCase confirmPasswordResetUseCase,
                          LogoutUseCase logoutUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.authenticateCredentialsUseCase = authenticateCredentialsUseCase;
        this.authenticateOAuthUseCase = authenticateOAuthUseCase;
        this.verifyTwoFactorUseCase = verifyTwoFactorUseCase;
        this.requestPasswordResetUseCase = requestPasswordResetUseCase;
        this.confirmPasswordResetUseCase = confirmPasswordResetUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserCommand command) {
        RegisterUserResponse response = registerUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginCommand command, HttpServletRequest request) {
        String ip = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        AuthResponse response = authenticateCredentialsUseCase.execute(command, ip, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/2fa/verify")
    public ResponseEntity<AuthResponse> verify2FA(@Valid @RequestBody Verify2FACommand command, HttpServletRequest request) {
        String ip = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        AuthResponse response = verifyTwoFactorUseCase.execute(command, ip, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/oauth/{provider}")
    public ResponseEntity<AuthResponse> oauthLogin(@PathVariable("provider") String providerStr,
                                                   @Valid @RequestBody OAuthLoginCommand command,
                                                   HttpServletRequest request) {
        AuthProvider provider = AuthProvider.valueOf(providerStr.toUpperCase());
        String ip = extractClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        AuthResponse response = authenticateOAuthUseCase.execute(provider, command.tokenProveedor(), ip, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<PasswordResetDTOs.SimpleMessageResponse> requestReset(
            @Valid @RequestBody PasswordResetDTOs.RequestResetCommand command) {
        PasswordResetDTOs.SimpleMessageResponse response = requestPasswordResetUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<PasswordResetDTOs.SimpleMessageResponse> confirmReset(
            @Valid @RequestBody PasswordResetDTOs.ConfirmResetCommand command) {
        PasswordResetDTOs.SimpleMessageResponse response = confirmPasswordResetUseCase.execute(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<PasswordResetDTOs.SimpleMessageResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        PasswordResetDTOs.SimpleMessageResponse response = logoutUseCase.execute(authHeader);
        return ResponseEntity.ok(response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
