package com.sportflow.features.security.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Permission {
    private UUID id;
    private String recurso;
    private PermissionOperation operacion;
    private String metodoHttp;
    private String rutaUrl;
    private String descripcion;
    private Instant creadoEn;

    public Permission(UUID id, String recurso, PermissionOperation operacion, String metodoHttp,
                      String rutaUrl, String descripcion, Instant creadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.recurso = validarNoVacio(recurso, "recurso").toUpperCase();
        this.operacion = Objects.requireNonNull(operacion, "operacion no puede ser nula");
        this.metodoHttp = validarNoVacio(metodoHttp, "metodoHttp").toUpperCase();
        this.rutaUrl = validarNoVacio(rutaUrl, "rutaUrl");
        this.descripcion = validarNoVacio(descripcion, "descripcion");
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
    }

    public static Permission crear(String recurso, PermissionOperation operacion, String metodoHttp,
                                   String rutaUrl, String descripcion) {
        return new Permission(UUID.randomUUID(), recurso, operacion, metodoHttp, rutaUrl, descripcion, Instant.now());
    }

    public String getCodigo() {
        return recurso + ":" + operacion.name();
    }

    private String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BusinessRuleException("INVALID_PERMISSION_FIELD", "El campo " + campo + " no puede ser nulo o vacío.");
        }
        return valor.trim();
    }

    public UUID getId() { return id; }
    public String getRecurso() { return recurso; }
    public PermissionOperation getOperacion() { return operacion; }
    public String getMetodoHttp() { return metodoHttp; }
    public String getRutaUrl() { return rutaUrl; }
    public String getDescripcion() { return descripcion; }
    public Instant getCreadoEn() { return creadoEn; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission that)) return false;
        return Objects.equals(id, that.id) || (Objects.equals(recurso, that.recurso) && operacion == that.operacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recurso, operacion);
    }
}
