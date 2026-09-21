package com.sportflow.features.security.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Role {
    private UUID id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private Set<Permission> permisos;
    private Instant creadoEn;
    private Instant actualizadoEn;

    public Role(UUID id, String nombre, String descripcion, boolean activo,
                Set<Permission> permisos, Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.nombre = validarNoVacio(nombre, "nombre").toUpperCase();
        this.descripcion = validarNoVacio(descripcion, "descripcion");
        this.activo = activo;
        this.permisos = permisos != null ? new HashSet<>(permisos) : new HashSet<>();
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public static Role crear(String nombre, String descripcion) {
        return new Role(UUID.randomUUID(), nombre, descripcion, true, new HashSet<>(), Instant.now(), Instant.now());
    }

    public void asignarPermiso(Permission permiso) {
        Objects.requireNonNull(permiso, "El permiso a asignar no puede ser nulo");
        this.permisos.add(permiso);
        this.actualizadoEn = Instant.now();
    }

    public void removerPermiso(Permission permiso) {
        Objects.requireNonNull(permiso, "El permiso a remover no puede ser nulo");
        this.permisos.remove(permiso);
        this.actualizadoEn = Instant.now();
    }

    public void sincronizarPermisos(Set<Permission> nuevosPermisos) {
        this.permisos.clear();
        if (nuevosPermisos != null) {
            this.permisos.addAll(nuevosPermisos);
        }
        this.actualizadoEn = Instant.now();
    }

    public void desactivar() {
        this.activo = false;
        this.actualizadoEn = Instant.now();
    }

    public void activar() {
        this.activo = true;
        this.actualizadoEn = Instant.now();
    }

    public void actualizar(String descripcion, boolean activo) {
        this.descripcion = validarNoVacio(descripcion, "descripcion");
        this.activo = activo;
        this.actualizadoEn = Instant.now();
    }

    private String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BusinessRuleException("INVALID_ROLE_FIELD", "El campo " + campo + " no puede ser nulo o vacío.");
        }
        return valor.trim();
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isActivo() { return activo; }
    public Set<Permission> getPermisos() { return Set.copyOf(permisos); }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return Objects.equals(id, role.id) || Objects.equals(nombre, role.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
