# 🗄️ Documento de Base de Datos - Módulo de Seguridad
**Proyecto:** SportFlow - Plataforma de Gestión de Eventos Deportivos  
**Módulo:** Seguridad y Control de Acceso (Security & IAM)  
**Versión:** 1.0.0  
**Motor SGBD:** PostgreSQL 15+ / 16  
**Normalización:** Tercera Forma Normal (3NF)  
**Convención:** Minúsculas, `snake_case`, nombres de tabla en plural, llaves primarias `UUID`  
**Autor:** Agente 3 - Principal DBA  

---

## 1. Script DDL Inmaculado (PostgreSQL)

```sql
-- ============================================================================
-- SCRIPT DDL: MÓDULO DE SEGURIDAD Y CONTROL DE ACCESO (SPORTFLOW)
-- MOTOR: PostgreSQL 15+
-- ============================================================================

-- Habilitar extensión para generación de identificadores universales (UUID v4)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ----------------------------------------------------------------------------
-- 1. TABLA: personas
-- Representa a las personas físicas naturales que interactúan con el sistema.
-- ----------------------------------------------------------------------------
CREATE TABLE personas (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tipo_documento VARCHAR(20) NOT NULL,
    numero_documento VARCHAR(50) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telefono VARCHAR(30),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_personas_documento UNIQUE (tipo_documento, numero_documento),
    CONSTRAINT uq_personas_email UNIQUE (email),
    CONSTRAINT chk_personas_estado CHECK (estado IN ('ACTIVO', 'INACTIVO', 'BLOQUEADO'))
);

CREATE INDEX idx_personas_email ON personas (email);
CREATE INDEX idx_personas_documento ON personas (numero_documento);

-- ----------------------------------------------------------------------------
-- 2. TABLA: usuarios
-- Cuenta de acceso y credenciales asociada a una persona física.
-- ----------------------------------------------------------------------------
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    persona_id UUID NOT NULL,
    nombre_usuario VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255),
    proveedor_auth VARCHAR(30) NOT NULL DEFAULT 'LOCAL',
    proveedor_id VARCHAR(150),
    dos_factores_habilitado BOOLEAN NOT NULL DEFAULT FALSE,
    dos_factores_secreto VARCHAR(100),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuarios_persona FOREIGN KEY (persona_id)
        REFERENCES personas (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uq_usuarios_persona_id UNIQUE (persona_id),
    CONSTRAINT uq_usuarios_nombre_usuario UNIQUE (nombre_usuario),
    CONSTRAINT uq_usuarios_email UNIQUE (email),
    CONSTRAINT chk_usuarios_proveedor CHECK (proveedor_auth IN ('LOCAL', 'GOOGLE', 'GITHUB', 'MICROSOFT')),
    CONSTRAINT chk_usuarios_estado CHECK (estado IN ('ACTIVO', 'INACTIVO', 'SUSPENDIDO', 'PENDIENTE_2FA'))
);

CREATE INDEX idx_usuarios_persona_id ON usuarios (persona_id);
CREATE INDEX idx_usuarios_email ON usuarios (email);
CREATE INDEX idx_usuarios_proveedor ON usuarios (proveedor_auth, proveedor_id);

-- ----------------------------------------------------------------------------
-- 3. TABLA: perfiles
-- Datos biográficos complementarios, de contacto y multimedia del usuario.
-- ----------------------------------------------------------------------------
CREATE TABLE perfiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL,
    telefono VARCHAR(30),
    foto_url VARCHAR(500),
    biografia TEXT,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_perfiles_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_perfiles_usuario_id UNIQUE (usuario_id)
);

CREATE INDEX idx_perfiles_usuario_id ON perfiles (usuario_id);

-- ----------------------------------------------------------------------------
-- 4. TABLA: roles
-- Catálogo de roles del sistema para control de acceso RBAC.
-- ----------------------------------------------------------------------------
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_roles_nombre UNIQUE (nombre)
);

CREATE INDEX idx_roles_nombre ON roles (nombre);

-- ----------------------------------------------------------------------------
-- 5. TABLA: permisos
-- Operaciones atómicas sobre recursos protegidos (Matriz de permisos).
-- ----------------------------------------------------------------------------
CREATE TABLE permisos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recurso VARCHAR(100) NOT NULL,
    operacion VARCHAR(30) NOT NULL,
    metodo_http VARCHAR(10) NOT NULL,
    ruta_url VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_permisos_recurso_operacion UNIQUE (recurso, operacion),
    CONSTRAINT chk_permisos_operacion CHECK (operacion IN ('VER', 'LISTAR', 'CREAR', 'ACTUALIZAR', 'ELIMINAR', 'EJECUTAR')),
    CONSTRAINT chk_permisos_metodo_http CHECK (metodo_http IN ('GET', 'POST', 'PUT', 'PATCH', 'DELETE'))
);

CREATE INDEX idx_permisos_recurso ON permisos (recurso);
CREATE INDEX idx_permisos_ruta_metodo ON permisos (ruta_url, metodo_http);

-- ----------------------------------------------------------------------------
-- 6. TABLA: usuario_roles
-- Relación M:N entre usuarios y roles con vigencia temporal.
-- ----------------------------------------------------------------------------
CREATE TABLE usuario_roles (
    usuario_id UUID NOT NULL,
    rol_id UUID NOT NULL,
    fecha_inicio DATE NOT NULL DEFAULT CURRENT_DATE,
    fecha_fin DATE,
    asignado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_roles_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_usuario_roles_rol FOREIGN KEY (rol_id)
        REFERENCES roles (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_usuario_roles_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
);

CREATE INDEX idx_usuario_roles_rol_id ON usuario_roles (rol_id);

-- ----------------------------------------------------------------------------
-- 7. TABLA: rol_permisos
-- Relación M:N entre roles y permisos (Matriz de autorización).
-- ----------------------------------------------------------------------------
CREATE TABLE rol_permisos (
    rol_id UUID NOT NULL,
    permiso_id UUID NOT NULL,
    asignado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (rol_id, permiso_id),
    CONSTRAINT fk_rol_permisos_rol FOREIGN KEY (rol_id)
        REFERENCES roles (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_rol_permisos_permiso FOREIGN KEY (permiso_id)
        REFERENCES permisos (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX idx_rol_permisos_permiso_id ON rol_permisos (permiso_id);

-- ----------------------------------------------------------------------------
-- 8. TABLA: sesiones
-- Registro de sesiones activas, tokens emitidos y control de revocación.
-- ----------------------------------------------------------------------------
CREATE TABLE sesiones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL,
    token_jti VARCHAR(100) NOT NULL,
    fecha_emision TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP WITH TIME ZONE NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    ip_origen VARCHAR(45),
    user_agent VARCHAR(255),
    CONSTRAINT fk_sesiones_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_sesiones_token_jti UNIQUE (token_jti)
);

CREATE INDEX idx_sesiones_usuario_activa ON sesiones (usuario_id, activa);
CREATE INDEX idx_sesiones_expiracion ON sesiones (fecha_expiracion);

-- ----------------------------------------------------------------------------
-- 9. TABLA: recuperaciones_password
-- Solicitudes de restablecimiento temporal de contraseña con token único.
-- ----------------------------------------------------------------------------
CREATE TABLE recuperaciones_password (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    fecha_expiracion TIMESTAMP WITH TIME ZONE NOT NULL,
    utilizado BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_recuperaciones_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_recuperaciones_token UNIQUE (token_hash)
);

CREATE INDEX idx_recuperaciones_usuario ON recuperaciones_password (usuario_id, utilizado);

-- ----------------------------------------------------------------------------
-- 10. TABLA: codigos_dos_factores
-- Desafíos de segundo factor (2FA) temporales de un solo uso.
-- ----------------------------------------------------------------------------
CREATE TABLE codigos_dos_factores (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    usuario_id UUID NOT NULL,
    codigo_hash VARCHAR(255) NOT NULL,
    fecha_expiracion TIMESTAMP WITH TIME ZONE NOT NULL,
    utilizado BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_codigos_2fa_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE INDEX idx_codigos_2fa_usuario ON codigos_dos_factores (usuario_id, utilizado);
```

---

## 2. Diccionario de Datos Exhaustivo

### 2.1. Entidad: `personas`
Almacena la identidad biográfica y legal de personas físicas en el ecosistema deportivo.

| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador universal único inmutable generado mediante `uuid_generate_v4()`. |
| `tipo_documento` | `VARCHAR(20)` | `NOT NULL` | Tipo de documento de identidad (ej. CC, TI, PASAPORTE, CE). |
| `numero_documento`| `VARCHAR(50)` | `NOT NULL` | Número único legal de identificación. |
| `nombres` | `VARCHAR(100)` | `NOT NULL` | Nombres de pila de la persona. |
| `apellidos` | `VARCHAR(100)` | `NOT NULL` | Apellidos de la persona. |
| `email` | `VARCHAR(150)` | `NOT NULL, UNIQUE` | Correo electrónico de contacto principal. |
| `telefono` | `VARCHAR(30)` | `NULL` | Línea de teléfono celular o fija. |
| `estado` | `VARCHAR(20)` | `NOT NULL, DEFAULT 'ACTIVO'` | Estado de la persona en el padrón (`ACTIVO`, `INACTIVO`, `BLOQUEADO`). |
| `creado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Marca temporal inmutable de registro. |
| `actualizado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Marca temporal de última modificación. |

*Restricciones compuestas:* `uq_personas_documento (tipo_documento, numero_documento)`.

---

### 2.2. Entidad: `usuarios`
Cuentas de autenticación e identidad lógica del sistema.

| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador único de la cuenta de usuario. |
| `persona_id` | `UUID` | `NOT NULL, UNIQUE, FK` | Referencia a `personas(id)`. Relación 1:1 estricta. `ON DELETE RESTRICT`. |
| `nombre_usuario` | `VARCHAR(100)` | `NOT NULL, UNIQUE` | Alias o username único para autenticación en plataforma. |
| `email` | `VARCHAR(150)` | `NOT NULL, UNIQUE` | Correo electrónico institucional o personal para login. |
| `password_hash` | `VARCHAR(255)` | `NULL` | Hash BCrypt (costo 12). Obligatorio si `proveedor_auth == 'LOCAL'`. `NULL` en OAuth. |
| `proveedor_auth` | `VARCHAR(30)` | `NOT NULL, DEFAULT 'LOCAL'` | Mecanismo de origen: `LOCAL`, `GOOGLE`, `GITHUB`, `MICROSOFT`. |
| `proveedor_id` | `VARCHAR(150)` | `NULL` | Identificador único provisto por el proveedor OAuth externo. |
| `dos_factores_habilitado` | `BOOLEAN` | `NOT NULL, DEFAULT FALSE` | Bandera que exige validación de 2FA tras verificar contraseña. |
| `dos_factores_secreto` | `VARCHAR(100)` | `NULL` | Secreto Base32 cifrado para verificación TOTP si está habilitado. |
| `estado` | `VARCHAR(20)` | `NOT NULL, DEFAULT 'ACTIVO'` | Estado de la cuenta: `ACTIVO`, `INACTIVO`, `SUSPENDIDO`, `PENDIENTE_2FA`. |
| `creado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Fecha y hora de creación de la cuenta. |
| `actualizado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Fecha y hora del último cambio. |

---

### 2.3. Entidad: `perfiles`
Información complementaria del usuario (desacoplada de las credenciales de acceso).

| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador único del perfil. |
| `usuario_id` | `UUID` | `NOT NULL, UNIQUE, FK` | Referencia a `usuarios(id)`. Máximo un perfil por usuario. `ON DELETE CASCADE`. |
| `telefono` | `VARCHAR(30)` | `NULL` | Teléfono de contacto específico del perfil. |
| `foto_url` | `VARCHAR(500)` | `NULL` | URI pública segura de la fotografía o avatar. |
| `biografia` | `TEXT` | `NULL` | Breve reseña o información adicional del usuario. |
| `actualizado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Fecha de última edición del perfil. |

---

### 2.4. Entidad: `roles`
Catálogo de roles para el esquema de control de acceso basado en roles (RBAC).

| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador único del rol. |
| `nombre` | `VARCHAR(50)` | `NOT NULL, UNIQUE` | Nombre canónico del rol (ej. `ADMIN`, `ORGANIZADOR`, `DELEGADO`, `AFICIONADO`). |
| `descripcion` | `VARCHAR(255)` | `NOT NULL` | Descripción de las responsabilidades asociadas al rol. |
| `activo` | `BOOLEAN` | `NOT NULL, DEFAULT TRUE` | Determina si el rol está disponible para asignación. |
| `creado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Auditoría de creación. |
| `actualizado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Auditoría de modificación. |

---

### 2.5. Entidad: `permisos`
Catálogo atómico de capacidades operativas sobre recursos del sistema.

| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador universal del permiso. |
| `recurso` | `VARCHAR(100)` | `NOT NULL` | Módulo o recurso protegido (ej. `USUARIOS`, `TORNEOS`, `APUESTAS`, `ESCENARIOS`). |
| `operacion` | `VARCHAR(30)` | `NOT NULL` | Verbo de negocio: `VER`, `LISTAR`, `CREAR`, `ACTUALIZAR`, `ELIMINAR`, `EJECUTAR`. |
| `metodo_http` | `VARCHAR(10)` | `NOT NULL` | Método HTTP asociado: `GET`, `POST`, `PUT`, `PATCH`, `DELETE`. |
| `ruta_url` | `VARCHAR(255)` | `NOT NULL` | Patrón de ruta API asegurado (ej. `/api/v1/users/**`). |
| `descripcion` | `VARCHAR(255)` | `NOT NULL` | Detalle explicativo de la acción autorizada. |
| `creado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Registro temporal. |

*Restricción compuesta:* `uq_permisos_recurso_operacion (recurso, operacion)`.

---

### 2.6. Entidad: `usuario_roles`
Tabla asociativa M:N que gobierna los roles asignados a cada usuario.

| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `usuario_id` | `UUID` | `PRIMARY KEY, FK, NOT NULL` | Referencia a `usuarios(id)`. `ON DELETE CASCADE`. |
| `rol_id` | `UUID` | `PRIMARY KEY, FK, NOT NULL` | Referencia a `roles(id)`. `ON DELETE RESTRICT`. |
| `fecha_inicio` | `DATE` | `NOT NULL, DEFAULT CURRENT_DATE` | Fecha a partir de la cual el rol cobra validez. |
| `fecha_fin` | `DATE` | `NULL` | Fecha de expiración opcional del rol asignado. |
| `asignado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Auditoría de asignación. |

---

### 2.7. Entidad: `rol_permisos`
Tabla asociativa M:N que implementa la matriz de autorización por rol.

| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `rol_id` | `UUID` | `PRIMARY KEY, FK, NOT NULL` | Referencia a `roles(id)`. `ON DELETE CASCADE`. |
| `permiso_id` | `UUID` | `PRIMARY KEY, FK, NOT NULL` | Referencia a `permisos(id)`. `ON DELETE CASCADE`. |
| `asignado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Auditoría de vinculación en la matriz. |

---

### 2.8. Entidad: `sesiones`
Persistencia de sesiones de autenticación para control de revocación inmediata.

| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador único de la sesión interna. |
| `usuario_id` | `UUID` | `NOT NULL, FK` | Referencia a `usuarios(id)`. `ON DELETE CASCADE`. |
| `token_jti` | `VARCHAR(100)` | `NOT NULL, UNIQUE` | JWT ID único universal emitido para el token de acceso. |
| `fecha_emision` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Momento de login exitoso. |
| `fecha_expiracion` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Momento exacto en que expira la validez del token. |
| `activa` | `BOOLEAN` | `NOT NULL, DEFAULT TRUE` | Bandera de estado. Al cerrar sesión o resetear clave se marca `FALSE`. |
| `ip_origen` | `VARCHAR(45)` | `NULL` | Dirección IP cliente (soporta IPv4 e IPv6). |
| `user_agent` | `VARCHAR(255)` | `NULL` | Cadena identificadora del navegador/dispositivo. |

---

### 2.9. Entidad: `recuperaciones_password`
Mecanismo temporal para restablecimiento seguro de contraseñas olvidadas.

| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador del registro de recuperación. |
| `usuario_id` | `UUID` | `NOT NULL, FK` | Usuario solicitante. `ON DELETE CASCADE`. |
| `token_hash` | `VARCHAR(255)` | `NOT NULL, UNIQUE` | Hash SHA-256 del token enviado al usuario por canal seguro. |
| `fecha_expiracion` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Tiempo límite de validez (ej. 15 minutos). |
| `utilizado` | `BOOLEAN` | `NOT NULL, DEFAULT FALSE` | Pasa a `TRUE` tras su uso, impidiendo reutilizaciones. |
| `creado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Creación de la solicitud. |

---

### 2.10. Entidad: `codigos_dos_factores`
Desafíos numéricos efímeros para doble factor de autenticación.

| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador del desafío 2FA. |
| `usuario_id` | `UUID` | `NOT NULL, FK` | Cuenta bajo autenticación. `ON DELETE CASCADE`. |
| `codigo_hash` | `VARCHAR(255)` | `NOT NULL` | Hash seguro del código OTP de 6 dígitos. |
| `fecha_expiracion` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Tiempo de vida corto (ej. 5 minutos). |
| `utilizado` | `BOOLEAN` | `NOT NULL, DEFAULT FALSE` | Invalida el código tras ser presentado exitosamente. |
| `creado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL, DEFAULT CURRENT_TIMESTAMP` | Creación del código. |

---

## 3. Script DDL: Entrega 2 (Gestión Deportiva y Competencias)

```sql
-- ============================================================================
-- SCRIPT DDL: ENTREGA 2 (MÓDULOS DE GESTIÓN DEPORTIVA Y COMPETENCIAS)
-- MOTOR: PostgreSQL 15+ (3NF con UUIDs)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. TABLA: deportes
-- Catálogo de disciplinas deportivas con soporte recursivo de subdeportes.
-- ----------------------------------------------------------------------------
CREATE TABLE deportes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre_canonico VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    deporte_padre_id UUID,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_deportes_nombre UNIQUE (nombre_canonico),
    CONSTRAINT fk_deportes_padre FOREIGN KEY (deporte_padre_id) 
        REFERENCES deportes(id) ON DELETE RESTRICT
);

CREATE INDEX idx_deportes_padre ON deportes (deporte_padre_id);

-- ----------------------------------------------------------------------------
-- 2. TABLA: clubes
-- Entidad corporativa / club deportivo que agrupa uno o varios equipos.
-- ----------------------------------------------------------------------------
CREATE TABLE clubes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre VARCHAR(150) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    datos_contacto VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_clubes_nombre_ciudad UNIQUE (nombre, ciudad)
);

-- ----------------------------------------------------------------------------
-- 3. TABLA: equipos
-- Escuadras deportivas participantes en torneos y disciplinas.
-- ----------------------------------------------------------------------------
CREATE TABLE equipos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    club_id UUID,
    nombre_distintivo VARCHAR(150) NOT NULL,
    ciudad VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    genero VARCHAR(20) NOT NULL DEFAULT 'MIXTO',
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_inscripcion DATE NOT NULL DEFAULT CURRENT_DATE,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_equipos_club FOREIGN KEY (club_id) 
        REFERENCES clubes(id) ON DELETE SET NULL,
    CONSTRAINT chk_equipos_genero CHECK (genero IN ('MASCULINO', 'FEMENINO', 'MIXTO')),
    CONSTRAINT chk_equipos_estado CHECK (estado IN ('ACTIVO', 'INACTIVO', 'SUSPENDIDO'))
);

CREATE INDEX idx_equipos_club ON equipos (club_id);

-- ----------------------------------------------------------------------------
-- 4. TABLA: equipo_deportes (M:N)
-- Disciplinas deportivas habilitadas para cada equipo.
-- ----------------------------------------------------------------------------
CREATE TABLE equipo_deportes (
    equipo_id UUID NOT NULL,
    deporte_id UUID NOT NULL,
    asignado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (equipo_id, deporte_id),
    CONSTRAINT fk_eqdep_equipo FOREIGN KEY (equipo_id) 
        REFERENCES equipos(id) ON DELETE CASCADE,
    CONSTRAINT fk_eqdep_deporte FOREIGN KEY (deporte_id) 
        REFERENCES deportes(id) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 5. TABLA: jugadores
-- Atletas y futbolistas registrados para conformar plantillas deportivas.
-- ----------------------------------------------------------------------------
CREATE TABLE jugadores (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    persona_id UUID,
    tipo_documento VARCHAR(20) NOT NULL,
    numero_identificacion VARCHAR(50) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    posicion VARCHAR(50),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_jugadores_identificacion UNIQUE (tipo_documento, numero_identificacion),
    CONSTRAINT fk_jugadores_persona FOREIGN KEY (persona_id) 
        REFERENCES personas(id) ON DELETE SET NULL,
    CONSTRAINT chk_jugadores_estado CHECK (estado IN ('ACTIVO', 'INACTIVO', 'SANCIONADO', 'LESIONADO'))
);

CREATE INDEX idx_jugadores_identificacion ON jugadores (numero_identificacion);

-- ----------------------------------------------------------------------------
-- 6. TABLA: contratos_jugadores (Historial de Traspasos)
-- Control inmutable de fichajes con fechas de inicio y fin.
-- ----------------------------------------------------------------------------
CREATE TABLE contratos_jugadores (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    jugador_id UUID NOT NULL,
    equipo_id UUID NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    numero_camiseta INT,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    observaciones TEXT,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_contratos_jugador FOREIGN KEY (jugador_id) 
        REFERENCES jugadores(id) ON DELETE RESTRICT,
    CONSTRAINT fk_contratos_equipo FOREIGN KEY (equipo_id) 
        REFERENCES equipos(id) ON DELETE RESTRICT,
    CONSTRAINT chk_contratos_estado CHECK (estado IN ('ACTIVO', 'FINALIZADO', 'RESCINDIDO'))
);

CREATE INDEX idx_contratos_jugador ON contratos_jugadores (jugador_id);
CREATE INDEX idx_contratos_equipo ON contratos_jugadores (equipo_id);

-- ----------------------------------------------------------------------------
-- 7. TABLA: habilidades
-- Catálogo de capacidades y atributos atléticos/técnicos.
-- ----------------------------------------------------------------------------
CREATE TABLE habilidades (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    nombre_canonico VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_habilidades_nombre UNIQUE (nombre_canonico)
);

-- ----------------------------------------------------------------------------
-- 8. TABLA: jugador_habilidades (M:N)
-- Asignación de habilidades técnicas y físicas a los jugadores.
-- ----------------------------------------------------------------------------
CREATE TABLE jugador_habilidades (
    jugador_id UUID NOT NULL,
    habilidad_id UUID NOT NULL,
    nivel VARCHAR(30) NOT NULL DEFAULT 'INTERMEDIO',
    observacion VARCHAR(255),
    fecha_registro DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (jugador_id, habilidad_id),
    CONSTRAINT fk_jh_jugador FOREIGN KEY (jugador_id) 
        REFERENCES jugadores(id) ON DELETE CASCADE,
    CONSTRAINT fk_jh_habilidad FOREIGN KEY (habilidad_id) 
        REFERENCES habilidades(id) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 9. TABLA: torneos
-- Competencias y torneos organizados en la plataforma.
-- ----------------------------------------------------------------------------
CREATE TABLE torneos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    deporte_id UUID NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    fecha_cierre_inscripcion DATE NOT NULL,
    cupo_equipos INT NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'REGISTRO_ABIERTO',
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_torneos_deporte FOREIGN KEY (deporte_id) 
        REFERENCES deportes(id) ON DELETE RESTRICT,
    CONSTRAINT chk_torneos_cupo CHECK (cupo_equipos > 1),
    CONSTRAINT chk_torneos_estado CHECK (estado IN ('REGISTRO_ABIERTO', 'REGISTRO_CERRADO', 'EN_CURSO', 'FINALIZADO', 'CANCELADO'))
);

CREATE INDEX idx_torneos_deporte ON torneos (deporte_id);

-- ----------------------------------------------------------------------------
-- 10. TABLA: inscripciones_torneos
-- Registro y validación de equipos admitidos en cada torneo.
-- ----------------------------------------------------------------------------
CREATE TABLE inscripciones_torneos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    torneo_id UUID NOT NULL,
    equipo_id UUID NOT NULL,
    fecha_inscripcion DATE NOT NULL DEFAULT CURRENT_DATE,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACEPTADA',
    observaciones VARCHAR(255),
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_inscripcion_torneo_equipo UNIQUE (torneo_id, equipo_id),
    CONSTRAINT fk_insc_torneo FOREIGN KEY (torneo_id) 
        REFERENCES torneos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_insc_equipo FOREIGN KEY (equipo_id) 
        REFERENCES equipos(id) ON DELETE RESTRICT,
    CONSTRAINT chk_insc_estado CHECK (estado IN ('PENDIENTE', 'ACEPTADA', 'RECHAZADA', 'CANCELADA'))
);

-- ----------------------------------------------------------------------------
-- 11. TABLA: fases
-- Etapas estructuradas en árbol jerárquico recursivo (grupos, llaves, final).
-- ----------------------------------------------------------------------------
CREATE TABLE fases (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    torneo_id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    orden INT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADA',
    fase_padre_id UUID,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_fases_torneo FOREIGN KEY (torneo_id) 
        REFERENCES torneos(id) ON DELETE CASCADE,
    CONSTRAINT fk_fases_padre FOREIGN KEY (fase_padre_id) 
        REFERENCES fases(id) ON DELETE RESTRICT,
    CONSTRAINT chk_fases_tipo CHECK (tipo IN ('GRUPOS', 'ELIMINATORIA_DIRECTA', 'LIGA', 'CUADRANGULAR')),
    CONSTRAINT chk_fases_estado CHECK (estado IN ('PROGRAMADA', 'EN_CURSO', 'FINALIZADA'))
);

CREATE INDEX idx_fases_torneo ON fases (torneo_id);
CREATE INDEX idx_fases_padre ON fases (fase_padre_id);

-- ----------------------------------------------------------------------------
-- 12. TABLA: grupos
-- Zonas / grupos dentro de una fase de competencia.
-- ----------------------------------------------------------------------------
CREATE TABLE grupos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    fase_id UUID NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    orden INT NOT NULL,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_grupos_fase_nombre UNIQUE (fase_id, nombre),
    CONSTRAINT fk_grupos_fase FOREIGN KEY (fase_id) 
        REFERENCES fases(id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- 13. TABLA: equipo_grupos (M:N)
-- Asignación de equipos en grupos de fase de grupos.
-- ----------------------------------------------------------------------------
CREATE TABLE equipo_grupos (
    grupo_id UUID NOT NULL,
    equipo_id UUID NOT NULL,
    fecha_asignacion DATE NOT NULL DEFAULT CURRENT_DATE,
    PRIMARY KEY (grupo_id, equipo_id),
    CONSTRAINT fk_eg_grupo FOREIGN KEY (grupo_id) 
        REFERENCES grupos(id) ON DELETE CASCADE,
    CONSTRAINT fk_eg_equipo FOREIGN KEY (equipo_id) 
        REFERENCES equipos(id) ON DELETE RESTRICT
);

-- ----------------------------------------------------------------------------
-- 14. TABLA: partidos
-- Calendario y programación de encuentros deportivos.
-- ----------------------------------------------------------------------------
CREATE TABLE partidos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    fase_id UUID NOT NULL,
    grupo_id UUID,
    equipo_local_id UUID,
    equipo_visitante_id UUID,
    fecha_hora_programada TIMESTAMP WITH TIME ZONE NOT NULL,
    escenario VARCHAR(150),
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADO',
    resultado_confirmado BOOLEAN NOT NULL DEFAULT FALSE,
    partido_origen_1 UUID,
    partido_origen_2 UUID,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_partidos_fase FOREIGN KEY (fase_id) 
        REFERENCES fases(id) ON DELETE RESTRICT,
    CONSTRAINT fk_partidos_grupo FOREIGN KEY (grupo_id) 
        REFERENCES grupos(id) ON DELETE SET NULL,
    CONSTRAINT fk_partidos_local FOREIGN KEY (equipo_local_id) 
        REFERENCES equipos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_partidos_visitante FOREIGN KEY (equipo_visitante_id) 
        REFERENCES equipos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_partidos_origen1 FOREIGN KEY (partido_origen_1) 
        REFERENCES partidos(id) ON DELETE SET NULL,
    CONSTRAINT fk_partidos_origen2 FOREIGN KEY (partido_origen_2) 
        REFERENCES partidos(id) ON DELETE SET NULL,
    CONSTRAINT chk_partidos_estado CHECK (estado IN ('PROGRAMADO', 'EN_CURSO', 'FINALIZADO', 'SUSPENDIDO', 'CANCELADO'))
);

CREATE INDEX idx_partidos_fase ON partidos (fase_id);
CREATE INDEX idx_partidos_equipos ON partidos (equipo_local_id, equipo_visitante_id);

-- ----------------------------------------------------------------------------
-- 15. TABLA: resultados_partidos
-- Marcador oficial confirmado para un encuentro deportivo.
-- ----------------------------------------------------------------------------
CREATE TABLE resultados_partidos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partido_id UUID NOT NULL,
    goles_local INT NOT NULL DEFAULT 0,
    goles_visitante INT NOT NULL DEFAULT 0,
    fecha_confirmacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    confirmado_por VARCHAR(100),
    observaciones TEXT,
    CONSTRAINT uq_resultados_partido UNIQUE (partido_id),
    CONSTRAINT fk_resultados_partido FOREIGN KEY (partido_id) 
        REFERENCES partidos(id) ON DELETE CASCADE,
    CONSTRAINT chk_resultados_goles CHECK (goles_local >= 0 AND goles_visitante >= 0)
);

-- ----------------------------------------------------------------------------
-- 16. TABLA: eventos_partidos
-- Anotaciones, goles y tarjetas registradas por minuto y jugador.
-- ----------------------------------------------------------------------------
CREATE TABLE eventos_partidos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    partido_id UUID NOT NULL,
    jugador_id UUID,
    equipo_id UUID NOT NULL,
    tipo_evento VARCHAR(30) NOT NULL,
    minuto INT NOT NULL,
    descripcion VARCHAR(255),
    estado_validacion VARCHAR(20) NOT NULL DEFAULT 'VALIDADO',
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eventos_partido FOREIGN KEY (partido_id) 
        REFERENCES partidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_eventos_jugador FOREIGN KEY (jugador_id) 
        REFERENCES jugadores(id) ON DELETE SET NULL,
    CONSTRAINT fk_eventos_equipo FOREIGN KEY (equipo_id) 
        REFERENCES equipos(id) ON DELETE RESTRICT,
    CONSTRAINT chk_eventos_minuto CHECK (minuto >= 0 AND minuto <= 150),
    CONSTRAINT chk_eventos_tipo CHECK (tipo_evento IN ('GOL', 'TARJETA_AMARILLA', 'TARJETA_ROJA', 'ASISTENCIA', 'AUTOGOL'))
);

CREATE INDEX idx_eventos_partido ON eventos_partidos (partido_id);

-- ----------------------------------------------------------------------------
-- 17. TABLA: clasificaciones (Tabla de Posiciones)
-- Registro acumulado de rendimiento de equipos en fases de torneo.
-- ----------------------------------------------------------------------------
CREATE TABLE clasificaciones (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    fase_id UUID NOT NULL,
    grupo_id UUID,
    equipo_id UUID NOT NULL,
    partidos_jugados INT NOT NULL DEFAULT 0,
    victorias INT NOT NULL DEFAULT 0,
    empates INT NOT NULL DEFAULT 0,
    derrotas INT NOT NULL DEFAULT 0,
    goles_favor INT NOT NULL DEFAULT 0,
    goles_contra INT NOT NULL DEFAULT 0,
    diferencia_goles INT NOT NULL DEFAULT 0,
    puntos INT NOT NULL DEFAULT 0,
    posicion INT NOT NULL DEFAULT 1,
    fecha_calculo TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_clasificacion_equipo UNIQUE (fase_id, grupo_id, equipo_id),
    CONSTRAINT fk_clasif_fase FOREIGN KEY (fase_id) 
        REFERENCES fases(id) ON DELETE CASCADE,
    CONSTRAINT fk_clasif_grupo FOREIGN KEY (grupo_id) 
        REFERENCES grupos(id) ON DELETE CASCADE,
    CONSTRAINT fk_clasif_equipo FOREIGN KEY (equipo_id) 
        REFERENCES equipos(id) ON DELETE RESTRICT
);

CREATE INDEX idx_clasificaciones_fase_grupo ON clasificaciones (fase_id, grupo_id);

-- ----------------------------------------------------------------------------
-- 18. TABLA: llaves (Playoffs / Cuadros Eliminatorios)
-- Árbol de enfrentamientos para fases de eliminación directa.
-- ----------------------------------------------------------------------------
CREATE TABLE llaves (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    fase_id UUID NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    ronda INT NOT NULL,
    orden INT NOT NULL,
    partido_id UUID,
    ganador_equipo_id UUID,
    creado_en TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_llaves_fase FOREIGN KEY (fase_id) 
        REFERENCES fases(id) ON DELETE CASCADE,
    CONSTRAINT fk_llaves_partido FOREIGN KEY (partido_id) 
        REFERENCES partidos(id) ON DELETE SET NULL,
    CONSTRAINT fk_llaves_ganador FOREIGN KEY (ganador_equipo_id) 
        REFERENCES equipos(id) ON DELETE SET NULL
);
```

---

## 4. Diccionario de Datos Exhaustivo: Entrega 2

### 4.1. Entidad: `deportes`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador único universal de la disciplina deportiva. |
| `nombre_canonico` | `VARCHAR(100)` | `NOT NULL, UNIQUE` | Nombre oficial (ej. Fútbol, Baloncesto, Voleibol). |
| `descripcion` | `TEXT` | `NULL` | Detalle o reglas generales de la disciplina. |
| `activo` | `BOOLEAN` | `NOT NULL, DEFAULT TRUE` | Indicador de disponibilidad operativa. |
| `deporte_padre_id` | `UUID` | `FK, NULL` | Autorreferencia recursiva a `deportes(id)`. ON DELETE RESTRICT. |
| `creado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Auditoría de creación. |
| `actualizado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Auditoría de modificación. |

### 4.2. Entidad: `clubes`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador del club deportivo. |
| `nombre` | `VARCHAR(150)` | `NOT NULL` | Nombre institucional del club. |
| `ciudad` | `VARCHAR(100)` | `NOT NULL` | Sede geográfica del club. |
| `datos_contacto` | `VARCHAR(255)` | `NULL` | Teléfono o correo institucional. |
| `activo` | `BOOLEAN` | `NOT NULL, DEFAULT TRUE` | Estado del club en la plataforma. |
| `creado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Auditoría de registro. |
| `actualizado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Auditoría de actualización. |

### 4.3. Entidad: `equipos`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador único de la plantilla deportiva. |
| `club_id` | `UUID` | `FK, NULL` | Club al que pertenece el equipo. ON DELETE SET NULL. |
| `nombre_distintivo` | `VARCHAR(150)` | `NOT NULL` | Nombre en competición (ej. "Los Halcones FC"). |
| `ciudad` | `VARCHAR(100)` | `NOT NULL` | Ciudad de origen de la plantilla. |
| `categoria` | `VARCHAR(50)` | `NOT NULL` | Categoría de edad/nivel (ej. "Sub-20", "Libre"). |
| `genero` | `VARCHAR(20)` | `NOT NULL, CHECK` | Género: `MASCULINO`, `FEMENINO`, `MIXTO`. |
| `estado` | `VARCHAR(20)` | `NOT NULL, CHECK` | Estado: `ACTIVO`, `INACTIVO`, `SUSPENDIDO`. |
| `fecha_inscripcion` | `DATE` | `NOT NULL` | Fecha de creación del equipo. |

### 4.4. Entidad: `equipo_deportes`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `equipo_id` | `UUID` | `PRIMARY KEY, FK, NOT NULL` | Equipo asociado. ON DELETE CASCADE. |
| `deporte_id` | `UUID` | `PRIMARY KEY, FK, NOT NULL` | Disciplina deportiva practicada. ON DELETE RESTRICT. |
| `asignado_en` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Auditoría de asignación. |

### 4.5. Entidad: `jugadores`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador del jugador. |
| `persona_id` | `UUID` | `FK, NULL` | Vinculación opcional con la entidad persona. |
| `tipo_documento` | `VARCHAR(20)` | `NOT NULL` | Tipo de documento (CC, TI, PASAPORTE, CE). |
| `numero_identificacion` | `VARCHAR(50)` | `NOT NULL, UNIQUE con tipo` | Número de cédula o pasaporte. |
| `nombres` | `VARCHAR(100)` | `NOT NULL` | Nombres del atleta. |
| `apellidos` | `VARCHAR(100)` | `NOT NULL` | Apellidos del atleta. |
| `fecha_nacimiento` | `DATE` | `NOT NULL` | Fecha de nacimiento para control de categorías. |
| `posicion` | `VARCHAR(50)` | `NULL` | Rol en el campo (ej. "Delantero", "Base"). |
| `estado` | `VARCHAR(20)` | `NOT NULL, CHECK` | Estado: `ACTIVO`, `INACTIVO`, `SANCIONADO`, `LESIONADO`. |

### 4.6. Entidad: `contratos_jugadores` (Historial de Traspasos)
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador del registro contractual / fichaje. |
| `jugador_id` | `UUID` | `NOT NULL, FK` | Jugador transferido. ON DELETE RESTRICT. |
| `equipo_id` | `UUID` | `NOT NULL, FK` | Equipo contratante. ON DELETE RESTRICT. |
| `fecha_inicio` | `DATE` | `NOT NULL` | Inicio de vigencia del contrato con el equipo. |
| `fecha_fin` | `DATE` | `NULL` | Fin de vigencia. NULL indica contrato activo actual. |
| `numero_camiseta` | `INT` | `NULL` | Dorsal oficial asignado en la plantilla. |
| `estado` | `VARCHAR(20)` | `NOT NULL, CHECK` | Estado: `ACTIVO`, `FINALIZADO`, `RESCINDIDO`. |
| `observaciones` | `TEXT` | `NULL` | Términos o notas de la transferencia. |

### 4.7. Entidad: `habilidades`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador de la habilidad. |
| `nombre_canonico` | `VARCHAR(100)` | `NOT NULL, UNIQUE` | Nombre de la capacidad (ej. "Velocidad", "Precisión de Pase"). |
| `descripcion` | `TEXT` | `NULL` | Descripción técnica de la métrica o habilidad. |
| `activo` | `BOOLEAN` | `NOT NULL, DEFAULT TRUE` | Disponibilidad en el catálogo. |

### 4.8. Entidad: `jugador_habilidades`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `jugador_id` | `UUID` | `PRIMARY KEY, FK, NOT NULL` | Jugador evaluado. ON DELETE CASCADE. |
| `habilidad_id` | `UUID` | `PRIMARY KEY, FK, NOT NULL` | Habilidad asociada. ON DELETE RESTRICT. |
| `nivel` | `VARCHAR(30)` | `NOT NULL` | Nivel evaluado (ej. "Avanzado", "Élite", "90/100"). |
| `observacion` | `VARCHAR(255)` | `NULL` | Anotaciones técnicas del entrenador/scout. |
| `fecha_registro` | `DATE` | `NOT NULL` | Fecha de evaluación. |

### 4.9. Entidad: `torneos`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador del torneo. |
| `deporte_id` | `UUID` | `NOT NULL, FK` | Disciplina del torneo. ON DELETE RESTRICT. |
| `nombre` | `VARCHAR(150)` | `NOT NULL` | Nombre oficial de la competencia. |
| `descripcion` | `TEXT` | `NULL` | Formato, bases y premiación. |
| `fecha_inicio` | `DATE` | `NOT NULL` | Inicio oficial de los encuentros. |
| `fecha_fin` | `DATE` | `NOT NULL` | Clausura del torneo. |
| `fecha_cierre_inscripcion` | `DATE` | `NOT NULL` | Límite para admisión de equipos. |
| `cupo_equipos` | `INT` | `NOT NULL, CHECK` | Cupo máximo permitido (`cupo_equipos > 1`). |
| `estado` | `VARCHAR(30)` | `NOT NULL, CHECK` | Estado: `REGISTRO_ABIERTO`, `REGISTRO_CERRADO`, `EN_CURSO`, `FINALIZADO`. |

### 4.10. Entidad: `inscripciones_torneos`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador de inscripción. |
| `torneo_id` | `UUID` | `NOT NULL, FK` | Torneo receptor. ON DELETE RESTRICT. |
| `equipo_id` | `UUID` | `NOT NULL, FK` | Equipo participante. ON DELETE RESTRICT. |
| `fecha_inscripcion` | `DATE` | `NOT NULL` | Fecha de radicación. |
| `estado` | `VARCHAR(20)` | `NOT NULL, CHECK` | Estado: `PENDIENTE`, `ACEPTADA`, `RECHAZADA`, `CANCELADA`. |
| `observaciones` | `VARCHAR(255)` | `NULL` | Motivo de aceptación/rechazo. |

### 4.11. Entidad: `fases`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador de la fase. |
| `torneo_id` | `UUID` | `NOT NULL, FK` | Torneo contenedor. ON DELETE CASCADE. |
| `nombre` | `VARCHAR(100)` | `NOT NULL` | Nombre (ej. "Fase de Grupos", "Octavos de Final"). |
| `tipo` | `VARCHAR(30)` | `NOT NULL, CHECK` | Tipo: `GRUPOS`, `ELIMINATORIA_DIRECTA`, `LIGA`. |
| `orden` | `INT` | `NOT NULL` | Secuencia cronológica en el torneo. |
| `estado` | `VARCHAR(20)` | `NOT NULL, CHECK` | Estado: `PROGRAMADA`, `EN_CURSO`, `FINALIZADA`. |
| `fase_padre_id` | `UUID` | `FK, NULL` | Árbol recursivo de fases. ON DELETE RESTRICT. |

### 4.12. Entidad: `grupos`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador del grupo. |
| `fase_id` | `UUID` | `NOT NULL, FK` | Fase contenedora. ON DELETE CASCADE. |
| `nombre` | `VARCHAR(50)` | `NOT NULL` | Nombre distintivo (ej. "Grupo A", "Grupo B"). |
| `orden` | `INT` | `NOT NULL` | Número de orden dentro de la fase. |

### 4.13. Entidad: `equipo_grupos`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `grupo_id` | `UUID` | `PRIMARY KEY, FK, NOT NULL` | Grupo asignado. ON DELETE CASCADE. |
| `equipo_id` | `UUID` | `PRIMARY KEY, FK, NOT NULL` | Equipo asignado. ON DELETE RESTRICT. |
| `fecha_asignacion` | `DATE` | `NOT NULL` | Fecha de sorteo/asignación. |

### 4.14. Entidad: `partidos`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador único del encuentro deportivo. |
| `fase_id` | `UUID` | `NOT NULL, FK` | Fase del torneo. ON DELETE RESTRICT. |
| `grupo_id` | `UUID` | `FK, NULL` | Grupo al que corresponde (si aplica). |
| `equipo_local_id` | `UUID` | `FK, NULL` | Equipo local (nullable en llaves pendientes). |
| `equipo_visitante_id` | `UUID` | `FK, NULL` | Equipo visitante (nullable en llaves pendientes). |
| `fecha_hora_programada`| `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Fecha y hora de inicio del partido. |
| `escenario` | `VARCHAR(150)` | `NULL` | Sede o cancha donde se disputa. |
| `estado` | `VARCHAR(20)` | `NOT NULL, CHECK` | Estado: `PROGRAMADO`, `EN_CURSO`, `FINALIZADO`, `SUSPENDIDO`. |
| `resultado_confirmado`| `BOOLEAN` | `NOT NULL, DEFAULT FALSE` | Bandera de cierre oficial del acta. |
| `partido_origen_1` | `UUID` | `FK, NULL` | Partido de procedencia para equipo local en playoffs. |
| `partido_origen_2` | `UUID` | `FK, NULL` | Partido de procedencia para equipo visitante en playoffs. |

### 4.15. Entidad: `resultados_partidos`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador del marcador oficial. |
| `partido_id` | `UUID` | `NOT NULL, UNIQUE, FK` | Encuentro disputado. ON DELETE CASCADE. |
| `goles_local` | `INT` | `NOT NULL, CHECK` | Goles/Puntos anotados por local (`>= 0`). |
| `goles_visitante` | `INT` | `NOT NULL, CHECK` | Goles/Puntos anotados por visitante (`>= 0`). |
| `fecha_confirmacion`| `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Momento de oficialización. |
| `confirmado_por` | `VARCHAR(100)` | `NULL` | Árbitro o comisario que suscribe el acta. |
| `observaciones` | `TEXT` | `NULL` | Incidencias del partido. |

### 4.16. Entidad: `eventos_partidos`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador del evento o anotación. |
| `partido_id` | `UUID` | `NOT NULL, FK` | Partido disputado. ON DELETE CASCADE. |
| `jugador_id` | `UUID` | `FK, NULL` | Jugador que ejecuta la acción (goleador/amonestado). |
| `equipo_id` | `UUID` | `NOT NULL, FK` | Equipo beneficiario o sancionado. |
| `tipo_evento` | `VARCHAR(30)` | `NOT NULL, CHECK` | Tipo: `GOL`, `TARJETA_AMARILLA`, `TARJETA_ROJA`, `ASISTENCIA`, `AUTOGOL`. |
| `minuto` | `INT` | `NOT NULL, CHECK` | Minuto cronometrado (0 a 150). |
| `descripcion` | `VARCHAR(255)` | `NULL` | Detalle (ej. "Tiro libre directo", "Falta táctica"). |
| `estado_validacion`| `VARCHAR(20)` | `NOT NULL` | Estado de validación arbitral. |

### 4.17. Entidad: `clasificaciones`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador del registro en tabla. |
| `fase_id` | `UUID` | `NOT NULL, FK` | Fase calculada. ON DELETE CASCADE. |
| `grupo_id` | `UUID` | `FK, NULL` | Grupo calculado (si aplica). |
| `equipo_id` | `UUID` | `NOT NULL, FK` | Equipo clasificado. |
| `partidos_jugados` | `INT` | `NOT NULL, DEFAULT 0` | Partidos disputados (PJ). |
| `victorias` | `INT` | `NOT NULL, DEFAULT 0` | Partidos ganados (PG). |
| `empates` | `INT` | `NOT NULL, DEFAULT 0` | Partidos empatados (PE). |
| `derrotas` | `INT` | `NOT NULL, DEFAULT 0` | Partidos perdidos (PP). |
| `goles_favor` | `INT` | `NOT NULL, DEFAULT 0` | Goles anotados (GF). |
| `goles_contra` | `INT` | `NOT NULL, DEFAULT 0` | Goles recibidos (GC). |
| `diferencia_goles`| `INT` | `NOT NULL, DEFAULT 0` | GF - GC (DG). |
| `puntos` | `INT` | `NOT NULL, DEFAULT 0` | Puntos totales: `(PG * 3) + (PE * 1)`. |
| `posicion` | `INT` | `NOT NULL, DEFAULT 1` | Puesto en la tabla ordenado por PTS, DG, GF. |
| `fecha_calculo` | `TIMESTAMP WITH TIME ZONE` | `NOT NULL` | Momento de la última actualización automática. |

### 4.18. Entidad: `llaves`
| Nombre de Columna | Tipo de Datos Exacto | Restricciones | Regla de Negocio / Descripción Detallada |
| :--- | :--- | :--- | :--- |
| `id` | `UUID` | `PRIMARY KEY, NOT NULL` | Identificador de la llave eliminatoria. |
| `fase_id` | `UUID` | `NOT NULL, FK` | Fase de eliminación. ON DELETE CASCADE. |
| `nombre` | `VARCHAR(100)` | `NOT NULL` | Nombre (ej. "Cuartos de Final 1", "Semifinal A"). |
| `ronda` | `INT` | `NOT NULL` | Nivel de la ronda (1 = dieciseisavos, 2 = octavos, etc.). |
| `orden` | `INT` | `NOT NULL` | Posición en el diagrama de llaves. |
| `partido_id` | `UUID` | `FK, NULL` | Partido asignado a la llave. |
| `ganador_equipo_id`| `UUID` | `FK, NULL` | Equipo vencedor que avanza a la siguiente ronda. |

