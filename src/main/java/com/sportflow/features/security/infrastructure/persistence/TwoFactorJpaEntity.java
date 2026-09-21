package com.sportflow.features.security.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "codigos_dos_factores")
public class TwoFactorJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "desafio_token", nullable = false, length = 100)
    private String desafioToken;

    @Column(name = "codigo_hash", nullable = false, length = 255)
    private String codigoHash;

    @Column(name = "fecha_expiracion", nullable = false)
    private Instant fechaExpiracion;

    @Column(name = "utilizado", nullable = false)
    private boolean utilizado;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public TwoFactorJpaEntity() {}

    public TwoFactorJpaEntity(UUID id, UUID usuarioId, String desafioToken, String codigoHash,
                              Instant fechaExpiracion, boolean utilizado, Instant creadoEn) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.desafioToken = desafioToken;
        this.codigoHash = codigoHash;
        this.fechaExpiracion = fechaExpiracion;
        this.utilizado = utilizado;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getDesafioToken() { return desafioToken; }
    public void setDesafioToken(String desafioToken) { this.desafioToken = desafioToken; }
    public String getCodigoHash() { return codigoHash; }
    public void setCodigoHash(String codigoHash) { this.codigoHash = codigoHash; }
    public Instant getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(Instant fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    public boolean isUtilizado() { return utilizado; }
    public void setUtilizado(boolean utilizado) { this.utilizado = utilizado; }
    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
