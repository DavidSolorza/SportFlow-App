package com.sportflow.features.security.domain.model;

import java.time.Instant;
import java.util.UUID;

public class UserProfile {
    private UUID id;
    private UUID usuarioId;
    private String telefono;
    private String fotoUrl;
    private String biografia;
    private Instant actualizadoEn;

    public UserProfile(UUID id, UUID usuarioId, String telefono, String fotoUrl, String biografia, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.telefono = telefono;
        this.fotoUrl = fotoUrl;
        this.biografia = biografia;
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public static UserProfile inicializar(UUID usuarioId, String telefono) {
        return new UserProfile(UUID.randomUUID(), usuarioId, telefono, null, null, Instant.now());
    }

    public void actualizar(String telefono, String fotoUrl, String biografia) {
        this.telefono = telefono;
        this.fotoUrl = fotoUrl;
        this.biografia = biografia;
        this.actualizadoEn = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getTelefono() { return telefono; }
    public String getFotoUrl() { return fotoUrl; }
    public String getBiografia() { return biografia; }
    public Instant getActualizadoEn() { return actualizadoEn; }
}
