package com.sportflow.features.security.infrastructure.persistence;

import com.sportflow.features.security.domain.model.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class SecurityEntityMapper {
    private SecurityEntityMapper() {}

    public static Person toDomain(PersonaJpaEntity entity) {
        if (entity == null) return null;
        return new Person(
                entity.getId(),
                entity.getTipoDocumento(),
                entity.getNumeroDocumento(),
                entity.getNombres(),
                entity.getApellidos(),
                entity.getEmail(),
                entity.getTelefono(),
                PersonStatus.valueOf(entity.getEstado()),
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }

    public static PersonaJpaEntity toEntity(Person domain) {
        if (domain == null) return null;
        return new PersonaJpaEntity(
                domain.getId(),
                domain.getTipoDocumento(),
                domain.getNumeroDocumento(),
                domain.getNombres(),
                domain.getApellidos(),
                domain.getEmail(),
                domain.getTelefono(),
                domain.getEstado().name(),
                domain.getCreadoEn(),
                domain.getActualizadoEn()
        );
    }

    public static UserProfile toDomain(PerfilJpaEntity entity) {
        if (entity == null) return null;
        return new UserProfile(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getTelefono(),
                entity.getFotoUrl(),
                entity.getBiografia(),
                entity.getActualizadoEn()
        );
    }

    public static PerfilJpaEntity toEntity(UserProfile domain) {
        if (domain == null) return null;
        return new PerfilJpaEntity(
                domain.getId(),
                domain.getUsuarioId(),
                domain.getTelefono(),
                domain.getFotoUrl(),
                domain.getBiografia(),
                domain.getActualizadoEn()
        );
    }

    public static Permission toDomain(PermisoJpaEntity entity) {
        if (entity == null) return null;
        return new Permission(
                entity.getId(),
                entity.getRecurso(),
                PermissionOperation.valueOf(entity.getOperacion()),
                entity.getMetodoHttp(),
                entity.getRutaUrl(),
                entity.getDescripcion(),
                entity.getCreadoEn()
        );
    }

    public static PermisoJpaEntity toEntity(Permission domain) {
        if (domain == null) return null;
        return new PermisoJpaEntity(
                domain.getId(),
                domain.getRecurso(),
                domain.getOperacion().name(),
                domain.getMetodoHttp(),
                domain.getRutaUrl(),
                domain.getDescripcion(),
                domain.getCreadoEn()
        );
    }

    public static Role toDomain(RolJpaEntity entity) {
        if (entity == null) return null;
        Set<Permission> permisos = entity.getPermisos() != null
                ? entity.getPermisos().stream().map(SecurityEntityMapper::toDomain).collect(Collectors.toSet())
                : new HashSet<>();

        return new Role(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.isActivo(),
                permisos,
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }

    public static RolJpaEntity toEntity(Role domain) {
        if (domain == null) return null;
        Set<PermisoJpaEntity> permisosEntities = domain.getPermisos() != null
                ? domain.getPermisos().stream().map(SecurityEntityMapper::toEntity).collect(Collectors.toSet())
                : new HashSet<>();

        return new RolJpaEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getDescripcion(),
                domain.isActivo(),
                permisosEntities,
                domain.getCreadoEn(),
                domain.getActualizadoEn()
        );
    }

    public static User toDomain(UsuarioJpaEntity entity, PerfilJpaEntity perfilEntity) {
        if (entity == null) return null;
        Person persona = toDomain(entity.getPersona());
        UserProfile perfil = toDomain(perfilEntity);

        Set<Role> roles = entity.getRoles() != null
                ? entity.getRoles().stream().map(SecurityEntityMapper::toDomain).collect(Collectors.toSet())
                : new HashSet<>();

        return new User(
                entity.getId(),
                persona,
                perfil,
                entity.getNombreUsuario(),
                entity.getEmail(),
                entity.getPasswordHash(),
                AuthProvider.valueOf(entity.getProveedorAuth()),
                entity.getProveedorId(),
                entity.isDosFactoresHabilitado(),
                entity.getDosFactoresSecreto(),
                UserStatus.valueOf(entity.getEstado()),
                roles,
                entity.getCreadoEn(),
                entity.getActualizadoEn()
        );
    }

    public static UsuarioJpaEntity toEntity(User domain) {
        if (domain == null) return null;
        PersonaJpaEntity personaEntity = toEntity(domain.getPersona());

        Set<RolJpaEntity> rolesEntities = domain.getRoles() != null
                ? domain.getRoles().stream().map(SecurityEntityMapper::toEntity).collect(Collectors.toSet())
                : new HashSet<>();

        return new UsuarioJpaEntity(
                domain.getId(),
                personaEntity,
                domain.getNombreUsuario(),
                domain.getEmail(),
                domain.getPasswordHash(),
                domain.getProveedorAuth().name(),
                domain.getProveedorId(),
                domain.isDosFactoresHabilitado(),
                domain.getDosFactoresSecreto(),
                domain.getEstado().name(),
                rolesEntities,
                domain.getCreadoEn(),
                domain.getActualizadoEn()
        );
    }

    public static UserSession toDomain(SesionJpaEntity entity) {
        if (entity == null) return null;
        return new UserSession(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getTokenJti(),
                entity.getFechaEmision(),
                entity.getFechaExpiracion(),
                entity.isActiva(),
                entity.getIpOrigen(),
                entity.getUserAgent()
        );
    }

    public static SesionJpaEntity toEntity(UserSession domain) {
        if (domain == null) return null;
        return new SesionJpaEntity(
                domain.getId(),
                domain.getUsuarioId(),
                domain.getTokenJti(),
                domain.getFechaEmision(),
                domain.getFechaExpiracion(),
                domain.isActiva(),
                domain.getIpOrigen(),
                domain.getUserAgent()
        );
    }

    public static TwoFactorChallenge toDomain(TwoFactorJpaEntity entity) {
        if (entity == null) return null;
        return new TwoFactorChallenge(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getDesafioToken(),
                entity.getCodigoHash(),
                entity.getFechaExpiracion(),
                entity.isUtilizado(),
                entity.getCreadoEn()
        );
    }

    public static TwoFactorJpaEntity toEntity(TwoFactorChallenge domain) {
        if (domain == null) return null;
        return new TwoFactorJpaEntity(
                domain.getId(),
                domain.getUsuarioId(),
                domain.getDesafioToken(),
                domain.getCodigoHash(),
                domain.getFechaExpiracion(),
                domain.isUtilizado(),
                domain.getCreadoEn()
        );
    }

    public static PasswordResetRequest toDomain(PasswordResetJpaEntity entity) {
        if (entity == null) return null;
        return new PasswordResetRequest(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getTokenHash(),
                entity.getFechaExpiracion(),
                entity.isUtilizado(),
                entity.getCreadoEn()
        );
    }

    public static PasswordResetJpaEntity toEntity(PasswordResetRequest domain) {
        if (domain == null) return null;
        return new PasswordResetJpaEntity(
                domain.getId(),
                domain.getUsuarioId(),
                domain.getTokenHash(),
                domain.getFechaExpiracion(),
                domain.isUtilizado(),
                domain.getCreadoEn()
        );
    }
}
