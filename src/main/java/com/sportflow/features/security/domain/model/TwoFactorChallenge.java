package com.sportflow.features.security.domain.model;

import java.time.Instant;
import java.util.UUID;

public class TwoFactorChallenge {
    private UUID id;
    private UUID usuarioId;
    private String desafioToken;
    private String codigoHash;
    private Instant fechaExpiracion;
    private boolean utilizado;
    private Instant creadoEn;

    public TwoFactorChallenge(UUID id, UUID usuarioId, String desafioToken, String codigoHash,
                              Instant fechaExpiracion, boolean utilizado, Instant creadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.desafioToken = desafioToken;
        this.codigoHash = codigoHash;
        this.fechaExpiracion = fechaExpiracion;
        this.utilizado = utilizado;
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static TwoFactorChallenge crear(UUID usuarioId, String desafioToken, String codigoHash, long duracionSegundos) {
        return new TwoFactorChallenge(UUID.randomUUID(), usuarioId, desafioToken, codigoHash,
                Instant.now().plusSeconds(duracionSegundos), false, Instant.now());
    }

    public boolean esValido() {
        return !this.utilizado && Instant.now().isBefore(this.fechaExpiracion);
    }

    public void marcarUtilizado() {
        this.utilizado = true;
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getDesafioToken() { return desafioToken; }
    public String getCodigoHash() { return codigoHash; }
    public Instant getFechaExpiracion() { return fechaExpiracion; }
    public boolean isUtilizado() { return utilizado; }
    public Instant getCreadoEn() { return creadoEn; }
}
