package com.sportflow.features.security.infrastructure.persistence;

import com.sportflow.features.security.domain.model.Permission;
import com.sportflow.features.security.domain.model.PermissionOperation;
import com.sportflow.features.security.domain.model.Person;
import com.sportflow.features.security.domain.model.Role;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.ports.PasswordEncoderPort;
import com.sportflow.features.security.domain.ports.PermissionRepositoryPort;
import com.sportflow.features.security.domain.ports.RoleRepositoryPort;
import com.sportflow.features.security.domain.ports.UserRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SecurityDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SecurityDataSeeder.class);

    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public SecurityDataSeeder(RoleRepositoryPort roleRepository,
                              PermissionRepositoryPort permissionRepository,
                              UserRepositoryPort userRepository,
                              PasswordEncoderPort passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (permissionRepository.findAll().isEmpty()) {
            log.info("Inicializando catálogo de permisos y roles del sistema SportFlow...");

            // 1. Crear permisos atómicos
            Permission pUserVer = permissionRepository.save(Permission.crear("USUARIOS", PermissionOperation.VER, "GET", "/api/v1/users/{id}", "Ver detalle de usuario"));
            Permission pUserListar = permissionRepository.save(Permission.crear("USUARIOS", PermissionOperation.LISTAR, "GET", "/api/v1/users", "Listar catálogo de usuarios"));
            Permission pUserCrear = permissionRepository.save(Permission.crear("USUARIOS", PermissionOperation.CREAR, "POST", "/api/v1/users", "Crear nuevo usuario administrativo"));
            Permission pUserActualizar = permissionRepository.save(Permission.crear("USUARIOS", PermissionOperation.ACTUALIZAR, "PUT", "/api/v1/users/{id}", "Actualizar usuario"));
            Permission pUserEliminar = permissionRepository.save(Permission.crear("USUARIOS", PermissionOperation.ELIMINAR, "DELETE", "/api/v1/users/{id}", "Desactivar usuario"));

            Permission pRolVer = permissionRepository.save(Permission.crear("ROLES", PermissionOperation.VER, "GET", "/api/v1/roles/{id}", "Ver rol"));
            Permission pRolListar = permissionRepository.save(Permission.crear("ROLES", PermissionOperation.LISTAR, "GET", "/api/v1/roles", "Listar roles"));
            Permission pRolCrear = permissionRepository.save(Permission.crear("ROLES", PermissionOperation.CREAR, "POST", "/api/v1/roles", "Crear rol"));
            Permission pRolActualizar = permissionRepository.save(Permission.crear("ROLES", PermissionOperation.ACTUALIZAR, "PUT", "/api/v1/roles/{id}", "Actualizar rol y permisos"));
            Permission pRolEliminar = permissionRepository.save(Permission.crear("ROLES", PermissionOperation.ELIMINAR, "DELETE", "/api/v1/roles/{id}", "Eliminar rol"));

            Permission pTorneoCrear = permissionRepository.save(Permission.crear("TORNEOS", PermissionOperation.CREAR, "POST", "/api/v1/tournaments", "Crear torneo deportivo"));
            Permission pTorneoListar = permissionRepository.save(Permission.crear("TORNEOS", PermissionOperation.LISTAR, "GET", "/api/v1/tournaments", "Listar torneos"));

            Permission pPartidoVer = permissionRepository.save(Permission.crear("PARTIDOS", PermissionOperation.VER, "GET", "/api/v1/matches/{id}", "Ver partido"));
            Permission pPartidoListar = permissionRepository.save(Permission.crear("PARTIDOS", PermissionOperation.LISTAR, "GET", "/api/v1/matches", "Listar partidos"));

            // 2. Crear roles
            Role adminRole = Role.crear("ADMIN", "Administrador supremo con acceso global al sistema");
            adminRole.sincronizarPermisos(new HashSet<>(List.of(
                    pUserVer, pUserListar, pUserCrear, pUserActualizar, pUserEliminar,
                    pRolVer, pRolListar, pRolCrear, pRolActualizar, pRolEliminar,
                    pTorneoCrear, pTorneoListar, pPartidoVer, pPartidoListar
            )));
            roleRepository.save(adminRole);

            Role organizadorRole = Role.crear("ORGANIZADOR", "Encargado de gestión deportiva y programación de partidos");
            organizadorRole.sincronizarPermisos(new HashSet<>(List.of(pTorneoCrear, pTorneoListar, pPartidoVer, pPartidoListar)));
            roleRepository.save(organizadorRole);

            Role delegadoRole = Role.crear("DELEGADO", "Representante oficial de equipos inscritos");
            roleRepository.save(delegadoRole);

            Role jugadorRole = Role.crear("JUGADOR", "Atleta participante en torneos y partidos");
            roleRepository.save(jugadorRole);

            Role aficionadoRole = Role.crear("AFICIONADO", "Usuario aficionado para compra de entradas y apuestas");
            aficionadoRole.sincronizarPermisos(new HashSet<>(List.of(pPartidoVer, pPartidoListar)));
            roleRepository.save(aficionadoRole);

            // 3. Crear usuario SuperAdmin por defecto
            Person adminPersona = Person.crear("CC", "00000000", "Administrador", "Principal", "admin@sportflow.com", "+573000000000");
            User adminUser = User.crearLocal(
                    adminPersona,
                    "admin",
                    "admin@sportflow.com",
                    passwordEncoder.encode("AdminPassword123!#")
            );
            adminUser.asignarRol(adminRole);
            userRepository.save(adminUser);

            log.info("Semilla de seguridad inicializada con éxito. Usuario SuperAdmin creado: admin@sportflow.com / AdminPassword123!#");
        }
    }
}
