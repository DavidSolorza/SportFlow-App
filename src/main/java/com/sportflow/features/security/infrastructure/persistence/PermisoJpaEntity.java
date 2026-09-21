package com.sportflow.features.security.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "permisos", uniqueConstraints = {
        @UniqueConstraint(name = "uq_permisos_recurso_operacion", columnNames = {"recurso", "operacion"})
})
public class PermisoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "recurso", nullable = false, length = 100)
    private String recurso;

    @Column(name = "operacion", nullable = false, length = 30)
    private String operacion;

    @Column(name = "metodo_http", nullable = false, length = 10)
    private String metodoHttp;

    @Column(name = "ruta_url", nullable = false, length = 255)
    private String rutaUrl;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    public PermisoJpaEntity() {}

    public PermisoJpaEntity(UUID id, String recurso, String operacion, String metodoHttp,
                            String rutaUrl, String descripcion, Instant creadoEn) {
        this.id = id;
        this.recurso = recurso;
        this.operacion = operacion;
        this.metodoHttp = metodoHttp;
        this.rutaUrl = rutaUrl;
        this.descripcion = descripcion;
        this.creadoEn = creadoEn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getRecurso() { return recurso; }
    public void setRecurso(String recurso) { this.recurso = recurso; }
    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }
    public String getMetodoHttp() { return metodoHttp; }
    public void setMetodoHttp(String metodoHttp) { this.metodoHttp = metodoHttp; }
    public String getRutaUrl() { return rutaUrl; }
    public void setRutaUrl(String rutaUrl) { this.rutaUrl = rutaUrl; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
