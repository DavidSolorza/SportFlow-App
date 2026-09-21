# 📄 Especificación de Contrato de API REST - Módulo de Seguridad
**Proyecto:** SportFlow - Plataforma de Gestión de Eventos Deportivos  
**Módulo:** Seguridad y Control de Acceso (Security & IAM)  
**Versión de API:** `v1`  
**Base Path:** `/api/v1`  
**Formato de Intercambio:** `application/json; charset=UTF-8`  
**Autor:** Agente 6 - Technical Writer & QA Specialist  

---

## 1. Estructura Estándar de Respuesta de Error

Todos los errores generados por la API devuelven una estructura uniforme:

```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "code": "SECURITY_ERROR_CODE",
  "message": "Mensaje legible y descriptivo del error o regla de negocio quebrantada.",
  "path": "/api/v1/auth/login",
  "details": [
    {
      "field": "password",
      "issue": "La contraseña debe contener al menos una mayúscula, un número y un símbolo."
    }
  ]
}
```

---

## 2. Catálogo Exhaustivo de Endpoints

---

### 2.1. HU-SE-07: Registrar usuario mediante credenciales
Permite el autoregistro de un nuevo usuario en la plataforma con contraseña protegida.

* **Ruta:** `POST /api/v1/auth/register`
* **Autenticación requerida:** Ninguna (Público).

#### Payload de Entrada (JSON)
```json
{
  "tipoDocumento": "CC",
  "numeroDocumento": "1098765432",
  "nombres": "Carlos Andrés",
  "apellidos": "Mendoza Gómez",
  "email": "carlos.mendoza@sportflow.com",
  "telefono": "+573001234567",
  "nombreUsuario": "cmendoza",
  "password": "Password123!#"
}
```

#### Matriz Estricta de Respuestas

##### `201 Created`
```json
{
  "userId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
  "personaId": "e3b0c442-98fc-1c14-9afbf4c8996fb924",
  "nombreUsuario": "cmendoza",
  "email": "carlos.mendoza@sportflow.com",
  "estado": "ACTIVO",
  "dosFactoresHabilitado": false,
  "mensaje": "Usuario registrado exitosamente. Ya puede iniciar sesión con sus credenciales."
}
```

##### `400 Bad Request`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "code": "EMAIL_ALREADY_REGISTERED",
  "message": "El correo electrónico ya se encuentra registrado en la plataforma.",
  "path": "/api/v1/auth/register",
  "details": []
}
```

##### `422 Unprocessable Entity`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 422,
  "error": "UNPROCESSABLE_ENTITY",
  "code": "VALIDATION_FAILED",
  "message": "Los datos enviados contienen errores de validación.",
  "path": "/api/v1/auth/register",
  "details": [
    {
      "field": "password",
      "issue": "La longitud mínima es de 8 caracteres con números y símbolos especiales."
    },
    {
      "field": "email",
      "issue": "El formato del correo electrónico es inválido."
    }
  ]
}
```

##### `500 Internal Server Error`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 500,
  "error": "INTERNAL_SERVER_ERROR",
  "code": "SYSTEM_UNEXPECTED_ERROR",
  "message": "Ocurrió una falla imprevista al procesar la solicitud. Contacte al administrador.",
  "path": "/api/v1/auth/register",
  "details": []
}
```

---

### 2.2. HU-SE-08: Iniciar Sesión con Credenciales (Email + Password)
Valida credenciales locales. Si el usuario tiene 2FA activado, emite un desafío temporal. Si no, emite el JWT y registra la sesión.

* **Ruta:** `POST /api/v1/auth/login`
* **Autenticación requerida:** Ninguna (Público).

#### Payload de Entrada (JSON)
```json
{
  "email": "carlos.mendoza@sportflow.com",
  "password": "Password123!#"
}
```

#### Matriz Estricta de Respuestas

##### `200 OK (Sin 2FA o 2FA no habilitado)`
```json
{
  "estado": "AUTENTICADO",
  "requiereSegundoFactor": false,
  "tokenAcceso": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipoToken": "Bearer",
  "expiraEnSegundos": 86400,
  "usuario": {
    "id": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
    "email": "carlos.mendoza@sportflow.com",
    "nombreCompleto": "Carlos Andrés Mendoza Gómez",
    "roles": ["ORGANIZADOR"],
    "permisos": ["TORNEOS:CREAR", "TORNEOS:LISTAR", "PARTIDOS:VER"]
  }
}
```

##### `200 OK (Requiere Segundo Factor - HU-SE-10)`
```json
{
  "estado": "PENDIENTE_2FA",
  "requiereSegundoFactor": true,
  "desafioToken": "temp-2fa-token-8f3e-4b2a-a91c-876543210abc",
  "expiraEnSegundos": 300,
  "mensaje": "Se requiere validación del segundo factor de autenticación."
}
```

##### `401 Unauthorized`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 401,
  "error": "UNAUTHORIZED",
  "code": "INVALID_CREDENTIALS",
  "message": "Las credenciales ingresadas son inválidas o la cuenta no existe.",
  "path": "/api/v1/auth/login",
  "details": []
}
```

##### `403 Forbidden`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 403,
  "error": "FORBIDDEN",
  "code": "ACCOUNT_DISABLED",
  "message": "La cuenta de usuario se encuentra inactiva o suspendida.",
  "path": "/api/v1/auth/login",
  "details": []
}
```

---

### 2.3. HU-SE-10: Verificar Segundo Factor (2FA)
Valida el código OTP de 6 dígitos asociado al desafío de autenticación previo.

* **Ruta:** `POST /api/v1/auth/2fa/verify`
* **Autenticación requerida:** Ninguna (Valida `desafioToken`).

#### Payload de Entrada (JSON)
```json
{
  "desafioToken": "temp-2fa-token-8f3e-4b2a-a91c-876543210abc",
  "codigo": "784920"
}
```

#### Matriz Estricta de Respuestas

##### `200 OK`
```json
{
  "estado": "AUTENTICADO",
  "tokenAcceso": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipoToken": "Bearer",
  "expiraEnSegundos": 86400,
  "usuario": {
    "id": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
    "email": "carlos.mendoza@sportflow.com",
    "nombreCompleto": "Carlos Andrés Mendoza Gómez",
    "roles": ["ORGANIZADOR"],
    "permisos": ["TORNEOS:CREAR", "TORNEOS:LISTAR"]
  }
}
```

##### `400 Bad Request`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "code": "INVALID_OR_EXPIRED_2FA_CODE",
  "message": "El código de verificación es incorrecto o ha expirado.",
  "path": "/api/v1/auth/2fa/verify",
  "details": []
}
```

---

### 2.4. HU-SE-08: Autenticación OAuth2 Nativa (Google / GitHub)
Intercambia el token de identidad externo llamando directamente al proveedor vía HTTP nativo sin SDKs comerciales.

* **Ruta:** `POST /api/v1/auth/oauth/{proveedor}`
* **Parámetros de Path:** `proveedor` (`google` o `github`).
* **Autenticación requerida:** Ninguna (Público).

#### Payload de Entrada (JSON)
```json
{
  "tokenProveedor": "ya29.a0AfH6SMCxyz123abc...",
  "tipoToken": "ID_TOKEN"
}
```

#### Matriz Estricta de Respuestas

##### `200 OK`
```json
{
  "estado": "AUTENTICADO",
  "tokenAcceso": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipoToken": "Bearer",
  "expiraEnSegundos": 86400,
  "usuario": {
    "id": "3c988a2e-5034-4bc9-bd89-8d19ab426189",
    "email": "jugador.pro@gmail.com",
    "nombreCompleto": "Fabián Castillo",
    "proveedorAuth": "GOOGLE",
    "roles": ["JUGADOR"],
    "permisos": ["PARTIDOS:VER"]
  }
}
```

##### `400 Bad Request`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "code": "OAUTH_PROVIDER_REJECTED",
  "message": "El token de autenticación provisto no es válido ante el proveedor de identidad externo.",
  "path": "/api/v1/auth/oauth/google",
  "details": []
}
```

##### `409 Conflict`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 409,
  "error": "CONFLICT",
  "code": "ACCOUNT_EXISTS_WITH_CREDENTIALS",
  "message": "El correo electrónico ya se encuentra registrado con contraseña tradicional. Ingrese con sus credenciales.",
  "path": "/api/v1/auth/oauth/google",
  "details": []
}
```

---

### 2.5. HU-SE-09: Solicitar Recuperación de Contraseña
Genera un token efímero con vigencia de 15 minutos para recuperación de acceso.

* **Ruta:** `POST /api/v1/auth/password-reset/request`
* **Autenticación requerida:** Ninguna.

#### Payload de Entrada (JSON)
```json
{
  "email": "carlos.mendoza@sportflow.com"
}
```

#### Matriz Estricta de Respuestas

##### `200 OK`
```json
{
  "mensaje": "Si el correo electrónico corresponde a una cuenta registrada con credenciales tradicionales, se ha enviado un enlace de recuperación.",
  "tiempoExpiracionMinutos": 15
}
```

##### `400 Bad Request`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "code": "OAUTH_ACCOUNT_RESET_FORBIDDEN",
  "message": "Las cuentas registradas mediante proveedores OAuth (Google/GitHub) deben gestionar sus contraseñas en la plataforma de dicho proveedor.",
  "path": "/api/v1/auth/password-reset/request",
  "details": []
}
```

---

### 2.6. HU-SE-09: Confirmar Restablecimiento de Contraseña
Establece la nueva clave e invalida todas las sesiones y tokens anteriores.

* **Ruta:** `POST /api/v1/auth/password-reset/confirm`
* **Autenticación requerida:** Ninguna.

#### Payload de Entrada (JSON)
```json
{
  "token": "reset-token-9a8b-7c6d-5e4f-3a2b1c0d",
  "nuevaPassword": "NewSecurePassword456!#"
}
```

#### Matriz Estricta de Respuestas

##### `200 OK`
```json
{
  "mensaje": "Contraseña actualizada exitosamente. Todas las sesiones activas han sido cerradas. Inicie sesión nuevamente."
}
```

##### `400 Bad Request`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "code": "INVALID_RESET_TOKEN",
  "message": "El token de recuperación es inválido, ya ha sido utilizado o ha expirado.",
  "path": "/api/v1/auth/password-reset/confirm",
  "details": []
}
```

---

### 2.7. HU-SE-08: Cerrar Sesión (Logout)
Invalida de inmediato el token de sesión en la base de datos de sesiones activas.

* **Ruta:** `POST /api/v1/auth/logout`
* **Autenticación requerida:** `Bearer <token>` (Cualquier usuario autenticado).

#### Matriz Estricta de Respuestas

##### `200 OK`
```json
{
  "mensaje": "Sesión cerrada correctamente. El token ha sido revocado."
}
```

##### `401 Unauthorized`
```json
{
  "timestamp": "2026-09-21T16:30:00.000Z",
  "status": 401,
  "error": "UNAUTHORIZED",
  "code": "TOKEN_EXPIRED_OR_REVOKED",
  "message": "El token provisto es inválido o su sesión ya ha sido cerrada.",
  "path": "/api/v1/auth/logout",
  "details": []
}
```

---

### 2.8. HU-SE-01 & HU-SE-02: Gestión de Usuarios y Perfiles

#### A. Listar Usuarios (Paginado)
* **Ruta:** `GET /api/v1/users?page=0&size=20&estado=ACTIVO`
* **Autenticación requerida:** `Bearer <token>` con permiso `USUARIOS:LISTAR`.

##### `200 OK`
```json
{
  "contenido": [
    {
      "id": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
      "nombreUsuario": "cmendoza",
      "email": "carlos.mendoza@sportflow.com",
      "nombres": "Carlos Andrés",
      "apellidos": "Mendoza Gómez",
      "estado": "ACTIVO",
      "proveedorAuth": "LOCAL",
      "dosFactoresHabilitado": false,
      "roles": ["ORGANIZADOR"]
    }
  ],
  "totalElementos": 1,
  "totalPaginas": 1,
  "paginaActual": 0
}
```

#### B. Consultar Detalle de Usuario y Perfil
* **Ruta:** `GET /api/v1/users/{userId}`
* **Autenticación requerida:** `Bearer <token>` con permiso `USUARIOS:VER` o el mismo usuario.

##### `200 OK`
```json
{
  "id": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
  "personaId": "e3b0c442-98fc-1c14-9afbf4c8996fb924",
  "tipoDocumento": "CC",
  "numeroDocumento": "1098765432",
  "nombres": "Carlos Andrés",
  "apellidos": "Mendoza Gómez",
  "email": "carlos.mendoza@sportflow.com",
  "telefono": "+573001234567",
  "nombreUsuario": "cmendoza",
  "estado": "ACTIVO",
  "proveedorAuth": "LOCAL",
  "dosFactoresHabilitado": false,
  "perfil": {
    "telefono": "+573001234567",
    "fotoUrl": "https://cdn.sportflow.com/avatars/cmendoza.webp",
    "biografia": "Coordinador de torneos departamentales de voleibol."
  },
  "roles": [
    {
      "id": "11111111-2222-3333-4444-555555555555",
      "nombre": "ORGANIZADOR",
      "fechaInicio": "2026-01-01",
      "fechaFin": null
    }
  ]
}
```

#### C. Actualizar Perfil de Usuario (HU-SE-02)
* **Ruta:** `PUT /api/v1/users/{userId}/profile`
* **Autenticación requerida:** `Bearer <token>` (Propietario o `USUARIOS:ACTUALIZAR`).

##### Payload de Entrada (JSON)
```json
{
  "telefono": "+573119876543",
  "fotoUrl": "https://cdn.sportflow.com/avatars/cmendoza_nuevo.webp",
  "biografia": "Organizador oficial del Torneo Intercolegiado 2026."
}
```

##### `200 OK`
```json
{
  "mensaje": "Perfil actualizado exitosamente.",
  "usuarioId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
  "perfil": {
    "telefono": "+573119876543",
    "fotoUrl": "https://cdn.sportflow.com/avatars/cmendoza_nuevo.webp",
    "biografia": "Organizador oficial del Torneo Intercolegiado 2026."
  }
}
```

---

### 2.9. HU-SE-03 & HU-SE-04: Gestión de Roles y Asignación

#### A. Crear Rol
* **Ruta:** `POST /api/v1/roles`
* **Autenticación requerida:** `Bearer <token>` con permiso `ROLES:CREAR`.

##### Payload de Entrada (JSON)
```json
{
  "nombre": "ARBITRO",
  "descripcion": "Juez principal o de línea autorizado para validar actas de partidos."
}
```

##### `201 Created`
```json
{
  "id": "7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d",
  "nombre": "ARBITRO",
  "descripcion": "Juez principal o de línea autorizado para validar actas de partidos.",
  "activo": true
}
```

#### B. Asignar Roles a Usuario (HU-SE-04)
* **Ruta:** `POST /api/v1/users/{userId}/roles`
* **Autenticación requerida:** `Bearer <token>` con permiso `ROLES:ACTUALIZAR`.

##### Payload de Entrada (JSON)
```json
{
  "rolIds": [
    "7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d"
  ],
  "fechaInicio": "2026-09-21",
  "fechaFin": null
}
```

##### `200 OK`
```json
{
  "mensaje": "Roles asignados exitosamente al usuario.",
  "usuarioId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
  "rolesActuales": ["ORGANIZADOR", "ARBITRO"]
}
```

---

### 2.10. HU-SE-05 & HU-SE-06: Permisos y Matriz de Autorización por Rol

#### A. Listar Catálogo de Permisos Agrupados por Recurso
* **Ruta:** `GET /api/v1/permissions`
* **Autenticación requerida:** `Bearer <token>` con permiso `PERMISOS:LISTAR`.

##### `200 OK`
```json
{
  "recursos": {
    "USUARIOS": [
      {"id": "p-1", "operacion": "VER", "metodoHttp": "GET", "rutaUrl": "/api/v1/users/{id}"},
      {"id": "p-2", "operacion": "LISTAR", "metodoHttp": "GET", "rutaUrl": "/api/v1/users"},
      {"id": "p-3", "operacion": "CREAR", "metodoHttp": "POST", "rutaUrl": "/api/v1/users"},
      {"id": "p-4", "operacion": "ACTUALIZAR", "metodoHttp": "PUT", "rutaUrl": "/api/v1/users/{id}"},
      {"id": "p-5", "operacion": "ELIMINAR", "metodoHttp": "DELETE", "rutaUrl": "/api/v1/users/{id}"}
    ],
    "TORNEOS": [
      {"id": "p-6", "operacion": "CREAR", "metodoHttp": "POST", "rutaUrl": "/api/v1/tournaments"}
    ]
  }
}
```

#### B. Asignar Matriz de Permisos a un Rol (HU-SE-06)
Sincroniza en un solo paso la matriz de permisos asociados al rol.

* **Ruta:** `PUT /api/v1/roles/{rolId}/permissions`
* **Autenticación requerida:** `Bearer <token>` con permiso `ROLES:ACTUALIZAR`.

##### Payload de Entrada (JSON)
```json
{
  "permisoIds": [
    "p-1",
    "p-2",
    "p-6"
  ]
}
```

##### `200 OK`
```json
{
  "mensaje": "Matriz de permisos actualizada exitosamente para el rol.",
  "rolId": "7a8b9c0d-1e2f-3a4b-5c6d-7e8f9a0b1c2d",
  "permisosAsignadosTotal": 3
}
```
