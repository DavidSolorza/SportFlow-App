package com.sportflow.features.security.domain.model;

import java.time.Instant;
import java.util.UUID;

public class PasswordResetRequest {
    private UUID id;
    private UUID usuarioId;
    private String tokenHash;
    private Instant fechaExpiracion;
    private boolean utilizado;
    private Instant creadoEn;

    public PasswordResetRequest(UUID id, UUID usuarioId, String tokenHash, Instant fechaExpiracion, boolean utilizado, Instant creadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.tokenHash = tokenHash;
        this.fechaExpiracion = fechaExpiracion;
        this.utilizado = utilizado;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static PasswordResetRequest crear(UUID usuarioId, String tokenHash, long minutosValidez) {
        return new PasswordResetRequest(UUID.randomUUID(), usuarioId, tokenHash,
                Instant.now().plusSeconds(minutosValidez * 60), false, Instant.now());
    }

    public boolean esValido() {
        return !this.utilizado && Instant.now().isBefore(this.fechaExpiracion);
    }

    public void consumir() {
        this.utilizado = true;
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public Instant getFechaExpiracion() { return fechaExpiracion; }
    public boolean isUtilizado() { return utilizado; }
    public Instant getCreadoEn() { return creadoEn; }
}
