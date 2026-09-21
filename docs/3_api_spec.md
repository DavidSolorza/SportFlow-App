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

---

## 3. Catálogo de Endpoints: Entrega 2 (Gestión Deportiva y Competencias)

---

### 3.1. Módulo de Gestión Deportiva (`HU-GD-01` a `HU-GD-08`)

#### 3.1.1. HU-GD-01: Gestionar deportes (con recursividad)
* **Crear Deporte:** `POST /api/v1/sports`
  * **Payload:**
    ```json
    {
      "nombreCanonico": "Fútbol de Salón",
      "descripcion": "Variante de fútbol en pista cubierta con 5 jugadores.",
      "deportePadreId": "11111111-2222-3333-4444-555555555555"
    }
    ```
  * **Respuesta `201 Created`:** Retorna el deporte creado con su jerarquía.
  * **Respuesta `409 Conflict`:** Nombre canónico duplicado.
* **Listar Deportes:** `GET /api/v1/sports?soloActivos=true`
* **Consultar Detalle:** `GET /api/v1/sports/{id}`
* **Consultar Jerarquía Recursiva:** `GET /api/v1/sports/{id}/hierarchy`
* **Actualizar Deporte:** `PUT /api/v1/sports/{id}`
* **Eliminar/Desactivar:** `DELETE /api/v1/sports/{id}`
  * **Respuesta `400 Bad Request`:** Si posee subdeportes, equipos o torneos asociados.

---

#### 3.1.2. HU-GD-02: Gestionar equipos
* **Crear Equipo:** `POST /api/v1/teams`
  * **Payload:**
    ```json
    {
      "nombreDistintivo": "Águilas Doradas FC",
      "ciudad": "Rionegro",
      "categoria": "Profesional",
      "genero": "MASCULINO",
      "clubId": null
    }
    ```
  * **Respuesta `201 Created`:** Objeto equipo con `id` UUID asignado.
* **Listar Equipos:** `GET /api/v1/teams`
* **Consultar Detalle:** `GET /api/v1/teams/{id}`
* **Actualizar Equipo:** `PUT /api/v1/teams/{id}`
* **Eliminar/Desactivar:** `DELETE /api/v1/teams/{id}`

---

#### 3.1.3. HU-GD-03: Asociar equipos con deportes (M:N)
* **Asociar Deporte a Equipo:** `POST /api/v1/teams/{teamId}/sports/{sportId}`
  * **Respuesta `201 Created`:** Vínculo creado exitosamente.
  * **Respuesta `409 Conflict`:** Asociación ya existente.
* **Retirar Asociación:** `DELETE /api/v1/teams/{teamId}/sports/{sportId}`
  * **Respuesta `200 OK`:** Vínculo retirado sin eliminar equipo ni deporte.
* **Consultar Deportes de un Equipo:** `GET /api/v1/teams/{teamId}/sports`
* **Consultar Equipos de un Deporte:** `GET /api/v1/sports/{sportId}/teams`

---

#### 3.1.4. HU-GD-04: Gestionar jugadores
* **Registrar Jugador:** `POST /api/v1/players`
  * **Payload:**
    ```json
    {
      "tipoDocumento": "CC",
      "numeroIdentificacion": "1020304050",
      "nombres": "Mateo",
      "apellidos": "Carvajal Gómez",
      "fechaNacimiento": "2000-05-15",
      "posicion": "Mediocampista Ofensivo"
    }
    ```
  * **Respuesta `201 Created`:** Jugador registrado.
  * **Respuesta `409 Conflict`:** Identificación duplicada.
* **Listar Jugadores:** `GET /api/v1/players`
* **Consultar Jugador:** `GET /api/v1/players/{id}`
* **Actualizar Jugador:** `PUT /api/v1/players/{id}`
* **Desactivar Jugador:** `DELETE /api/v1/players/{id}`

---

#### 3.1.5. HU-GD-05: Asignar jugadores a equipos (Historial de Traspasos)
* **Asignar Fichaje / Contrato:** `POST /api/v1/players/{playerId}/contracts`
  * **Payload:**
    ```json
    {
      "equipoId": "bbbbbbbb-2222-3333-4444-555555555555",
      "fechaInicio": "2026-01-10",
      "fechaFin": "2027-12-31",
      "numeroCamiseta": 10,
      "observaciones": "Transferencia definitiva por 2 temporadas."
    }
    ```
  * **Respuesta `201 Created`:** Cierra contrato anterior si existía y activa el nuevo, preservando historial inmutable.
* **Finalizar Contrato (Traspaso/Rescisión):** `PUT /api/v1/players/{playerId}/contracts/{contractId}/terminate`
  * **Payload:** `{"fechaFin": "2026-09-20", "motivo": "Transferencia al exterior"}`
* **Consultar Historial de Traspasos:** `GET /api/v1/players/{playerId}/transfers`
* **Consultar Plantilla de un Equipo:** `GET /api/v1/teams/{teamId}/roster`

---

#### 3.1.6. HU-GD-06: Gestionar habilidades deportivas
* **Registrar Habilidad:** `POST /api/v1/skills`
  * **Payload:**
    ```json
    {
      "nombreCanonico": "Definición en Mano a Mano",
      "descripcion": "Efectividad al definir frente al arquero en jugadas aisladas."
    }
    ```
  * **Respuesta `201 Created`:** Habilidad catalogada.
* **Listar Habilidades:** `GET /api/v1/skills`
* **Consultar Detalle:** `GET /api/v1/skills/{id}`
* **Actualizar Habilidad:** `PUT /api/v1/skills/{id}`
* **Eliminar Habilidad:** `DELETE /api/v1/skills/{id}` (valida no estar en uso)

---

#### 3.1.7. HU-GD-07: Asignar habilidades a jugadores (M:N)
* **Asignar Habilidad:** `POST /api/v1/players/{playerId}/skills`
  * **Payload:**
    ```json
    {
      "habilidadId": "cccccccc-2222-3333-4444-555555555555",
      "nivel": "ÉLITE",
      "observacion": "Excelente control y tiempo de remate."
    }
    ```
* **Retirar Habilidad:** `DELETE /api/v1/players/{playerId}/skills/{skillId}`
* **Listar Habilidades del Jugador:** `GET /api/v1/players/{playerId}/skills`
* **Listar Jugadores con Determinada Habilidad:** `GET /api/v1/skills/{skillId}/players`

---

#### 3.1.8. HU-GD-08: Consultar perfil deportivo centralizado
* **Ruta:** `GET /api/v1/players/{playerId}/profile`
* **Respuesta `200 OK`:**
  ```json
  {
    "jugador": {
      "id": "aaaa1111-2222-3333-4444-555555555555",
      "documento": "CC 1020304050",
      "nombres": "Mateo Carvajal Gómez",
      "fechaNacimiento": "2000-05-15",
      "posicion": "Mediocampista Ofensivo",
      "estado": "ACTIVO"
    },
    "equipoActual": {
      "equipoId": "bbbbbbbb-2222-3333-4444-555555555555",
      "nombreEquipo": "Águilas Doradas FC",
      "dorsal": 10,
      "fechaInicio": "2026-01-10",
      "fechaFin": "2027-12-31"
    },
    "historialTraspasos": [
      {
        "equipoId": "99999999-2222-3333-4444-555555555555",
        "nombreEquipo": "Envigado Cantera",
        "dorsal": 18,
        "fechaInicio": "2024-01-01",
        "fechaFin": "2025-12-31",
        "estado": "FINALIZADO"
      }
    ],
    "habilidades": [
      {
        "habilidadId": "cccccccc-2222-3333-4444-555555555555",
        "nombre": "Definición en Mano a Mano",
        "nivel": "ÉLITE",
        "observacion": "Excelente control y tiempo de remate."
      }
    ]
  }
  ```

---

### 3.2. Módulo de Gestión de Competencias (`HU-GC-01` a `HU-GC-08`)

#### 3.2.1. HU-GC-01: Gestionar torneos
* **Crear Torneo:** `POST /api/v1/tournaments`
  * **Payload:**
    ```json
    {
      "nombre": "Copa Metropolitana SportFlow 2026",
      "descripcion": "Torneo oficial de fútbol categoría libre con fase de grupos y eliminación directa.",
      "deporteId": "11111111-2222-3333-4444-555555555555",
      "fechaInicio": "2026-10-01",
      "fechaFin": "2026-12-15",
      "fechaCierreInscripcion": "2026-09-25",
      "cupoEquipos": 16
    }
    ```
* **Listar Torneos:** `GET /api/v1/tournaments`
* **Consultar Torneo:** `GET /api/v1/tournaments/{id}`
* **Actualizar Torneo:** `PUT /api/v1/tournaments/{id}`
* **Eliminar/Cancelar:** `DELETE /api/v1/tournaments/{id}`

---

#### 3.2.2. HU-GC-02: Gestionar inscripciones de equipos
* **Inscribir Equipo:** `POST /api/v1/tournaments/{tournamentId}/registrations`
  * **Payload:** `{"equipoId": "bbbbbbbb-2222-3333-4444-555555555555", "observaciones": "Inscripción en regla"}`
  * **Validaciones:** Verifica que el torneo esté en `REGISTRO_ABIERTO`, fecha previa al cierre, cupo disponible y que el equipo tenga habilitado el deporte del torneo.
* **Listar Inscripciones:** `GET /api/v1/tournaments/{tournamentId}/registrations`

---

#### 3.2.3. HU-GC-03: Gestionar fases de torneo (recursivas)
* **Crear Fase:** `POST /api/v1/tournaments/{tournamentId}/phases`
  * **Payload:**
    ```json
    {
      "nombre": "Fase de Grupos",
      "tipo": "GRUPOS",
      "orden": 1,
      "fasePadreId": null
    }
    ```
* **Listar Fases del Torneo:** `GET /api/v1/tournaments/{tournamentId}/phases`

---

#### 3.2.4. HU-GC-04: Gestionar grupos y asignación de equipos
* **Crear Grupo en Fase:** `POST /api/v1/phases/{phaseId}/groups`
  * **Payload:** `{"nombre": "Grupo A", "orden": 1}`
* **Asignar Equipo al Grupo:** `POST /api/v1/phases/{phaseId}/groups/{groupId}/teams/{teamId}`
  * **Validaciones:** Evita registrar el mismo equipo dos veces en la fase.
* **Listar Grupos y Equipos:** `GET /api/v1/phases/{phaseId}/groups`

---

#### 3.2.5. HU-GC-05: Gestionar fases finales y llaves eliminatorias
* **Crear Llave Eliminatoria:** `POST /api/v1/phases/{phaseId}/brackets`
  * **Payload:**
    ```json
    {
      "nombre": "Semifinal 1",
      "ronda": 2,
      "orden": 1,
      "partidoId": "dddddddd-2222-3333-4444-555555555555"
    }
    ```
* **Consultar Cuadro de Llaves:** `GET /api/v1/phases/{phaseId}/brackets`

---

#### 3.2.6. HU-GC-06: Programar partidos
* **Programar Partido:** `POST /api/v1/matches`
  * **Payload:**
    ```json
    {
      "faseId": "eeeeeeee-2222-3333-4444-555555555555",
      "grupoId": "ffffffff-2222-3333-4444-555555555555",
      "equipoLocalId": "bbbbbbbb-2222-3333-4444-555555555555",
      "equipoVisitanteId": "99999999-2222-3333-4444-555555555555",
      "fechaHoraProgramada": "2026-10-05T16:00:00Z",
      "escenario": "Estadio Atanasio Girardot - Cancha 1"
    }
    ```
* **Listar Partidos:** `GET /api/v1/matches?faseId={faseId}`
* **Consultar Partido:** `GET /api/v1/matches/{id}`

---

#### 3.2.7. HU-GC-07: Resultados y eventos de partidos
* **Registrar Resultado Oficial:** `POST /api/v1/matches/{id}/result`
  * **Payload:**
    ```json
    {
      "golesLocal": 2,
      "golesVisitante": 1,
      "confirmadoPor": "Árbitro Central Wilmar Roldán",
      "observaciones": "Partido disputado sin incidentes graves."
    }
    ```
  * **Efecto:** Actualiza automáticamente la tabla de posiciones si es fase de grupos o avanza el ganador si es llave eliminatoria.
* **Registrar Evento / Anotación:** `POST /api/v1/matches/{id}/events`
  * **Payload:**
    ```json
    {
      "jugadorId": "aaaa1111-2222-3333-4444-555555555555",
      "equipoId": "bbbbbbbb-2222-3333-4444-555555555555",
      "tipoEvento": "GOL",
      "minuto": 34,
      "descripcion": "Remate potente al ángulo superior derecho tras asistencia."
    }
    ```
* **Consultar Eventos del Encuentro:** `GET /api/v1/matches/{id}/events`

---

#### 3.2.8. HU-GC-08: Consultar desarrollo del torneo
* **Consultar Estado Integral del Torneo:** `GET /api/v1/tournaments/{id}/development`
  * **Respuesta `200 OK`:**
    ```json
    {
      "torneoId": "77777777-2222-3333-4444-555555555555",
      "nombre": "Copa Metropolitana SportFlow 2026",
      "estado": "EN_CURSO",
      "fases": [
        {
          "faseId": "eeeeeeee-2222-3333-4444-555555555555",
          "nombre": "Fase de Grupos",
          "tipo": "GRUPOS",
          "grupos": [
            {
              "grupoId": "ffffffff-2222-3333-4444-555555555555",
              "nombre": "Grupo A",
              "tablaPosiciones": [
                {
                  "posicion": 1,
                  "equipoId": "bbbbbbbb-2222-3333-4444-555555555555",
                  "nombreEquipo": "Águilas Doradas FC",
                  "partidosJugados": 1,
                  "victorias": 1,
                  "empates": 0,
                  "derrotas": 0,
                  "golesFavor": 2,
                  "golesContra": 1,
                  "diferenciaGoles": 1,
                  "puntos": 3
                }
              ]
            }
          ]
        }
      ]
    }
    ```
* **Consultar Tabla de Posiciones por Fase/Grupo:** `GET /api/v1/phases/{phaseId}/standings`

