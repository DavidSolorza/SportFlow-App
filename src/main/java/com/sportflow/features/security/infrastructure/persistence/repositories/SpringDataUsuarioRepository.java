package com.sportflow.features.security.infrastructure.persistence.repositories;

import com.sportflow.features.security.infrastructure.persistence.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataUsuarioRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
    Optional<UsuarioJpaEntity> findByEmail(String email);
    Optional<UsuarioJpaEntity> findByNombreUsuario(String nombreUsuario);
    Optional<UsuarioJpaEntity> findByProveedorAuthAndProveedorId(String proveedorAuth, String proveedorId);
    boolean existsByEmail(String email);
    boolean existsByNombreUsuario(String nombreUsuario);

    @Query("SELECT COUNT(u) > 0 FROM UsuarioJpaEntity u JOIN u.roles r WHERE r.id = :roleId")
    boolean existsUserWithRole(@Param("roleId") UUID roleId);
}
