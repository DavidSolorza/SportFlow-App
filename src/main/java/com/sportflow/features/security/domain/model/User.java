package com.sportflow.features.security.domain.model;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.UnauthorizedException;
import com.sportflow.features.security.domain.ports.PasswordEncoderPort;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class User {
    private UUID id;
    private Person persona;
    private UserProfile perfil;
    private String nombreUsuario;
    private String email;
    private String passwordHash;
    private AuthProvider proveedorAuth;
    private String proveedorId;
    private boolean dosFactoresHabilitado;
    private String dosFactoresSecreto;
    private UserStatus estado;
    private Set<Role> roles;
    private Instant creadoEn;
    private Instant actualizadoEn;

    public User(UUID id, Person persona, UserProfile perfil, String nombreUsuario, String email,
                String passwordHash, AuthProvider proveedorAuth, String proveedorId,
                boolean dosFactoresHabilitado, String dosFactoresSecreto, UserStatus estado,
                Set<Role> roles, Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.persona = Objects.requireNonNull(persona, "La persona asociada no puede ser nula");
        this.perfil = perfil;
        this.nombreUsuario = validarNoVacio(nombreUsuario, "nombreUsuario");
        this.email = validarNoVacio(email, "email").toLowerCase();
        this.passwordHash = passwordHash;
        this.proveedorAuth = proveedorAuth != null ? proveedorAuth : AuthProvider.LOCAL;
        this.proveedorId = proveedorId;
        this.dosFactoresHabilitado = dosFactoresHabilitado;
        this.dosFactoresSecreto = dosFactoresSecreto;
        this.estado = estado != null ? estado : UserStatus.ACTIVO;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public static User crearLocal(Person persona, String nombreUsuario, String email, String passwordHash) {
        UUID userId = UUID.randomUUID();
        UserProfile nuevoPerfil = UserProfile.inicializar(userId, persona.getTelefono());
        return new User(userId, persona, nuevoPerfil, nombreUsuario, email, passwordHash,
                AuthProvider.LOCAL, null, false, null, UserStatus.ACTIVO,
                new HashSet<>(), Instant.now(), Instant.now());
    }

    public static User crearOAuth(Person persona, String nombreUsuario, String email, AuthProvider provider, String providerId) {
        UUID userId = UUID.randomUUID();
        UserProfile nuevoPerfil = UserProfile.inicializar(userId, persona.getTelefono());
        return new User(userId, persona, nuevoPerfil, nombreUsuario, email, null,
                provider, providerId, false, null, UserStatus.ACTIVO,
                new HashSet<>(), Instant.now(), Instant.now());
    }

    public boolean puedeAutenticarseConPassword() {
        return this.proveedorAuth == AuthProvider.LOCAL && this.passwordHash != null;
    }

    public void validarContrasena(String rawPassword, PasswordEncoderPort hasher) {
        if (!puedeAutenticarseConPassword()) {
            throw new BusinessRuleException("OAUTH_USER_NO_PASSWORD", "Las cuentas creadas mediante OAuth deben autenticarse con su proveedor externo correspondiente.");
        }
        if (this.estado != UserStatus.ACTIVO) {
            throw new UnauthorizedException("USER_INACTIVE", "La cuenta se encuentra inactiva o suspendida.");
        }
        if (!hasher.matches(rawPassword, this.passwordHash)) {
            throw new UnauthorizedException("INVALID_CREDENTIALS", "Las credenciales ingresadas son inválidas.");
        }
    }

    public void habilitar2FA(String secreto) {
        if (this.proveedorAuth != AuthProvider.LOCAL) {
            throw new BusinessRuleException("2FA_NOT_ALLOWED_OAUTH", "El segundo factor interno aplica únicamente a cuentas tradicionales con correo y contraseña.");
        }
        this.dosFactoresHabilitado = true;
        this.dosFactoresSecreto = secreto;
        this.actualizadoEn = Instant.now();
    }

    public void deshabilitar2FA() {
        this.dosFactoresHabilitado = false;
        this.dosFactoresSecreto = null;
        this.actualizadoEn = Instant.now();
    }

    public void cambiarPassword(String nuevoHash) {
        if (this.proveedorAuth != AuthProvider.LOCAL) {
            throw new BusinessRuleException("OAUTH_PASSWORD_RESET_FORBIDDEN", "No es posible asignar contraseña a un usuario registrado mediante OAuth.");
        }
        this.passwordHash = nuevoHash;
        this.actualizadoEn = Instant.now();
    }

    public void asignarRol(Role rol) {
        Objects.requireNonNull(rol, "El rol no puede ser nulo");
        this.roles.add(rol);
        this.actualizadoEn = Instant.now();
    }

    public void removerRol(Role rol) {
        Objects.requireNonNull(rol, "El rol no puede ser nulo");
        this.roles.remove(rol);
        this.actualizadoEn = Instant.now();
    }

    public void sincronizarRoles(Set<Role> nuevosRoles) {
        this.roles.clear();
        if (nuevosRoles != null) {
            this.roles.addAll(nuevosRoles);
        }
        this.actualizadoEn = Instant.now();
    }

    public boolean tienePermiso(String recurso, PermissionOperation operacion) {
        String codigoBuscado = recurso.toUpperCase() + ":" + operacion.name();
        return this.roles.stream()
                .filter(Role::isActivo)
                .flatMap(r -> r.getPermisos().stream())
                .anyMatch(p -> p.getCodigo().equalsIgnoreCase(codigoBuscado));
    }

    public Set<String> obtenerNombresRoles() {
        return this.roles.stream()
                .filter(Role::isActivo)
                .map(Role::getNombre)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> obtenerCodigosPermisos() {
        return this.roles.stream()
                .filter(Role::isActivo)
                .flatMap(r -> r.getPermisos().stream())
                .map(Permission::getCodigo)
                .collect(Collectors.toUnmodifiableSet());
    }

    public void desactivar() {
        this.estado = UserStatus.INACTIVO;
        this.actualizadoEn = Instant.now();
    }

    public void activar() {
        this.estado = UserStatus.ACTIVO;
        this.actualizadoEn = Instant.now();
    }

    public void asignarPerfil(UserProfile perfil) {
        this.perfil = perfil;
    }

    private String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BusinessRuleException("INVALID_USER_FIELD", "El campo " + campo + " no puede ser nulo o vacío.");
        }
        return valor.trim();
    }

    // Getters
    public UUID getId() { return id; }
    public Person getPersona() { return persona; }
    public UserProfile getPerfil() { return perfil; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public AuthProvider getProveedorAuth() { return proveedorAuth; }
    public String getProveedorId() { return proveedorId; }
    public boolean isDosFactoresHabilitado() { return dosFactoresHabilitado; }
    public String getDosFactoresSecreto() { return dosFactoresSecreto; }
    public UserStatus getEstado() { return estado; }
    public Set<Role> getRoles() { return Set.copyOf(roles); }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
}
