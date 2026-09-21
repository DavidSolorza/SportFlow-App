package com.sportflow.features.sports.domain.model;

import com.sportflow.core.errors.BusinessRuleException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Sport {
    private final UUID id;
    private String nombreCanonico;
    private String descripcion;
    private boolean activo;
    private UUID deportePadreId;
    private final List<Sport> subdeportes;
    private final Instant creadoEn;
    private Instant actualizadoEn;

    public Sport(UUID id, String nombreCanonico, String descripcion, boolean activo,
                 UUID deportePadreId, List<Sport> subdeportes, Instant creadoEn, Instant actualizadoEn) {
        this.id = id != null ? id : UUID.randomUUID();
        this.nombreCanonico = validarNoVacio(nombreCanonico, "nombreCanonico");
        this.descripcion = descripcion;
        this.activo = activo;
        this.deportePadreId = deportePadreId;
        this.subdeportes = subdeportes != null ? new ArrayList<>(subdeportes) : new ArrayList<>();
        this.creadoEn = creadoEn != null ? creadoEn : Instant.now();
        this.actualizadoEn = actualizadoEn != null ? actualizadoEn : Instant.now();
    }

    public static Sport crear(String nombreCanonico, String descripcion, UUID deportePadreId) {
        return new Sport(UUID.randomUUID(), nombreCanonico, descripcion, true, deportePadreId,
                new ArrayList<>(), Instant.now(), Instant.now());
    }

    public void actualizar(String nombreCanonico, String descripcion, Boolean activo, UUID deportePadreId) {
        if (deportePadreId != null && deportePadreId.equals(this.id)) {
            throw new BusinessRuleException("Un deporte no puede ser subdeporte de sí mismo.");
        }
        this.nombreCanonico = validarNoVacio(nombreCanonico, "nombreCanonico");
        this.descripcion = descripcion;
        if (activo != null) {
            this.activo = activo;
        }
        this.deportePadreId = deportePadreId;
        this.actualizadoEn = Instant.now();
    }

    public void agregarSubdeporte(Sport subdeporte) {
        if (subdeporte == null) {
            throw new BusinessRuleException("El subdeporte no puede ser nulo.");
        }
        if (subdeporte.getId().equals(this.id)) {
            throw new BusinessRuleException("Un deporte no puede ser subdeporte de sí mismo.");
        }
        this.subdeportes.add(subdeporte);
        this.actualizadoEn = Instant.now();
    }

    /**
     * Recorrido recursivo del árbol genealógico para obtener todos los descendientes en profundidad.
     */
    public List<Sport> obtenerDescendientes() {
        List<Sport> descendientes = new ArrayList<>();
        recorrerDescendientesRecursivo(this, descendientes);
        return Collections.unmodifiableList(descendientes);
    }

    private void recorrerDescendientesRecursivo(Sport actual, List<Sport> acumulador) {
        for (Sport hijo : actual.getSubdeportes()) {
            acumulador.add(hijo);
            recorrerDescendientesRecursivo(hijo, acumulador);
        }
    }

    public void desactivar() {
        this.activo = false;
        this.actualizadoEn = Instant.now();
    }

    public void activar() {
        this.activo = true;
        this.actualizadoEn = Instant.now();
    }

    private String validarNoVacio(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BusinessRuleException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }

    public UUID getId() {
        return id;
    }

    public String getNombreCanonico() {
        return nombreCanonico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public UUID getDeportePadreId() {
        return deportePadreId;
    }

    public List<Sport> getSubdeportes() {
        return Collections.unmodifiableList(subdeportes);
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }
}
