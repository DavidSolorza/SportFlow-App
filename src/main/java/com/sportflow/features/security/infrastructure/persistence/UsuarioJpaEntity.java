package com.sportflow.features.security.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "persona_id", nullable = false, unique = true)
    private PersonaJpaEntity persona;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 100)
    private String nombreUsuario;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "proveedor_auth", nullable = false, length = 30)
    private String proveedorAuth;

    @Column(name = "proveedor_id", length = 150)
    private String proveedorId;

    @Column(name = "dos_factores_habilitado", nullable = false)
    private boolean dosFactoresHabilitado;

    @Column(name = "dos_factores_secreto", length = 100)
    private String dosFactoresSecreto;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<RolJpaEntity> roles = new HashSet<>();

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    public UsuarioJpaEntity() {}

    public UsuarioJpaEntity(UUID id, PersonaJpaEntity persona, String nombreUsuario, String email,
                            String passwordHash, String proveedorAuth, String proveedorId,
                            boolean dosFactoresHabilitado, String dosFactoresSecreto, String estado,
                            Set<RolJpaEntity> roles, Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.persona = persona;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.passwordHash = passwordHash;
        this.proveedorAuth = proveedorAuth;
        this.proveedorId = proveedorId;
        this.dosFactoresHabilitado = dosFactoresHabilitado;
        this.dosFactoresSecreto = dosFactoresSecreto;
        this.estado = estado;
        this.roles = roles != null ? roles : new HashSet<>();
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public PersonaJpaEntity getPersona() { return persona; }
    public void setPersona(PersonaJpaEntity persona) { this.persona = persona; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getProveedorAuth() { return proveedorAuth; }
    public void setProveedorAuth(String proveedorAuth) { this.proveedorAuth = proveedorAuth; }
    public String getProveedorId() { return proveedorId; }
    public void setProveedorId(String proveedorId) { this.proveedorId = proveedorId; }
    public boolean isDosFactoresHabilitado() { return dosFactoresHabilitado; }
    public void setDosFactoresHabilitado(boolean dosFactoresHabilitado) { this.dosFactoresHabilitado = dosFactoresHabilitado; }
    public String getDosFactoresSecreto() { return dosFactoresSecreto; }
    public void setDosFactoresSecreto(String dosFactoresSecreto) { this.dosFactoresSecreto = dosFactoresSecreto; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Set<RolJpaEntity> getRoles() { return roles; }
    public void setRoles(Set<RolJpaEntity> roles) { this.roles = roles; }
    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
