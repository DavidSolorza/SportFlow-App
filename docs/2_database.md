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
