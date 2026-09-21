package com.sportflow.features.security.domain.model;

import java.time.Instant;
import java.util.UUID;

public class UserSession {
    private UUID id;
    private UUID usuarioId;
    private String tokenJti;
    private Instant fechaEmision;
    private Instant fechaExpiracion;
    private boolean activa;
    private String ipOrigen;
    private String userAgent;

    public UserSession(UUID id, UUID usuarioId, String tokenJti, Instant fechaEmision,
                       Instant fechaExpiracion, boolean activa, String ipOrigen, String userAgent) {
        this.id = id != null ? id : UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.tokenJti = tokenJti;
        this.fechaEmision = fechaEmision != null ? fechaEmision : Instant.now();
        this.fechaExpiracion = fechaExpiracion;
        this.activa = activa;
        this.ipOrigen = ipOrigen;
        this.userAgent = userAgent;
    }

    public static UserSession iniciar(UUID usuarioId, String tokenJti, Instant fechaExpiracion, String ipOrigen, String userAgent) {
        return new UserSession(UUID.randomUUID(), usuarioId, tokenJti, Instant.now(), fechaExpiracion, true, ipOrigen, userAgent);
    }

    public boolean esValida() {
        return this.activa && Instant.now().isBefore(this.fechaExpiracion);
    }

    public void revocar() {
        this.activa = false;
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getTokenJti() { return tokenJti; }
    public Instant getFechaEmision() { return fechaEmision; }
    public Instant getFechaExpiracion() { return fechaExpiracion; }
    public boolean isActiva() { return activa; }
    public String getIpOrigen() { return ipOrigen; }
    public String getUserAgent() { return userAgent; }
}
