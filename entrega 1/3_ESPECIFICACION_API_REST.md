# 📄 Especificación de Contratos API REST (Entrega 1)
**Proyecto:** SportFlow - Plataforma de Gestión de Eventos Deportivos  
**Módulo:** Seguridad y Control de Acceso  
**Versión de API:** `v1`  
**Base Path:** `http://localhost:8080/api/v1`  
**Formato de Intercambio:** `application/json; charset=UTF-8`  
**Autor:** Agente 6 - Technical Writer & QA Specialist  
**Versión:** 1.0.0  

---

## 1. Estándar Global de Manejo de Errores

Todas las excepciones generadas por la API son interceptadas por el `GlobalExceptionHandler` y devuelven una estructura uniforme:

```json
{
  "timestamp": "2026-09-21T17:00:00.000Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "code": "SECURITY_ERROR_CODE",
  "message": "Descripción comprensible y amigable del error.",
  "path": "/api/v1/auth/login",
  "details": [
    {
      "field": "password",
      "issue": "La contraseña debe tener al menos 8 caracteres, mayúscula, minúscula y número."
    }
  ]
}
```

---

## 2. Catálogo Detallado de Endpoints

### 2.1. Autenticación y Registro

#### `POST /api/v1/auth/register` (HU-SE-07)
Registra un nuevo usuario con credenciales locales en la plataforma.
* **Autenticación:** Ninguna (Público).
* **Payload Entrada:**
  ```json
  {
    "tipoDocumento": "CC",
    "numeroDocumento": "1098765432",
    "nombres": "Mateo",
    "apellidos": "Gómez Pérez",
    "telefono": "+573001234567",
    "email": "mateo.gomez@sportflow.com",
    "password": "PasswordSegura123!"
  }
  ```
* **Respuestas:**
  - `201 Created`:
    ```json
    {
      "id": "e8b2b2e1-7c19-4a92-963d-4c8e76319842",
      "email": "mateo.gomez@sportflow.com",
      "nombreCompleto": "Mateo Gómez Pérez",
      "mensaje": "Usuario registrado exitosamente en SportFlow."
    }
    ```
  - `409 Conflict`: `{"code": "EMAIL_ALREADY_REGISTERED", "message": "El correo ya se encuentra registrado."}`
  - `422 Unprocessable Entity`: Errores de validación en campos obligatorios o formato de contraseña.

---

#### `POST /api/v1/auth/login` (HU-SE-08 & HU-SE-10)
Inicia sesión mediante correo electrónico y contraseña.
* **Autenticación:** Ninguna (Público).
* **Payload Entrada:**
  ```json
  {
    "email": "admin@sportflow.com",
    "password": "AdminPassword123!#"
  }
  ```
* **Respuestas:**
  - `200 OK (Sin 2FA):`
    ```json
    {
      "estado": "AUTENTICADO",
      "requiereSegundoFactor": false,
      "tokenAcceso": "eyJhbGciOiJIUzUxMiJ9...",
      "tipoToken": "Bearer",
      "expiraEnSegundos": 86400,
      "usuario": {
        "id": "6081d3a3-5a47-4ca0-9ea2-319b2332ed27",
        "email": "admin@sportflow.com",
        "nombreCompleto": "Administrador Principal",
        "roles": ["ADMIN"],
        "permisos": ["TORNEOS:CREAR", "USUARIOS:LISTAR", "ROLES:CREAR"]
      }
    }
    ```
  - `200 OK (Con 2FA Habilitado):`
    ```json
    {
      "estado": "REQUIERE_SEGUNDO_FACTOR",
      "requiereSegundoFactor": true,
      "desafioToken": "550e8400-e29b-41d4-a716-446655440000",
      "mensaje": "Se ha generado un código de verificación para completar su acceso."
    }
    ```
  - `401 Unauthorized`: `{"code": "INVALID_CREDENTIALS", "message": "Credenciales inválidas."}`

---

#### `POST /api/v1/auth/2fa/verify` (HU-SE-10)
Resuelve el desafío de segundo factor enviando el token del desafío y el código OTP.
* **Autenticación:** Ninguna (Público).
* **Payload Entrada:**
  ```json
  {
    "desafioToken": "550e8400-e29b-41d4-a716-446655440000",
    "codigo": "123456"
  }
  ```
* **Respuestas:**
  - `200 OK`: Devuelve el token JWT final y el usuario autenticado.
  - `401 Unauthorized`: `{"code": "INVALID_2FA_CODE", "message": "El código OTP es inválido o ha expirado."}`

---

#### `POST /api/v1/auth/oauth/{provider}` (HU-SE-08)
Autenticación federada mediante OAuth2 nativo (Google o GitHub).
* **Parámetro de Ruta:** `provider` (`google` o `github`).
* **Payload Entrada:**
  ```json
  {
    "tokenProveedor": "Ov23lisRZCCGHj7BNSVt_code_or_token",
    "tipoToken": "BEARER_TOKEN"
  }
  ```
* **Respuestas:**
  - `200 OK`: Devuelve JWT emitido por SportFlow y auto-provisiona el usuario si no existía.
  - `409 Conflict`: Si el correo ya existía bajo credenciales locales tradicionales (`ACCOUNT_EXISTS_WITH_CREDENTIALS`).
  - `401 Unauthorized`: Si el proveedor externo rechaza las credenciales (`OAUTH_PROVIDER_REJECTED`).

---

#### `POST /api/v1/auth/password-reset/request` (HU-SE-09)
Solicita un enlace o token temporal de recuperación de contraseña.
* **Payload Entrada:**
  ```json
  {
    "email": "atleta@sportflow.com"
  }
  ```
* **Respuestas:**
  - `200 OK`: `{"mensaje": "Si el correo se encuentra registrado, se enviarán instrucciones de recuperación."}`

---

#### `POST /api/v1/auth/password-reset/confirm` (HU-SE-09)
Establece una nueva contraseña utilizando el token de reseteo e invalida todas las sesiones previas.
* **Payload Entrada:**
  ```json
  {
    "token": "reset-uuid-token-string",
    "nuevaPassword": "NuevaPassword123!#"
  }
  ```
* **Respuestas:**
  - `200 OK`: `{"mensaje": "Contraseña actualizada exitosamente. Sus sesiones anteriores han sido revocadas."}`
  - `400 Bad Request`: `{"code": "RESET_TOKEN_INVALID_OR_EXPIRED"}`

---

#### `POST /api/v1/auth/logout` (HU-SE-08)
Revoca la sesión activa en el servidor y marca el token como inactivo.
* **Cabecera:** `Authorization: Bearer <JWT>`
* **Respuestas:**
  - `200 OK`: `{"mensaje": "Sesión cerrada exitosamente."}`

---

### 2.2. Gestión de Usuarios y Perfiles

#### `GET /api/v1/users` (HU-SE-01)
Lista paginada de usuarios con filtros por estado o rol.
* **Cabecera:** `Authorization: Bearer <JWT>` (Requiere permiso `USUARIOS:LISTAR`).
* **Query Params:** `page=0&size=10&status=ACTIVE`
* **Respuestas:**
  - `200 OK`: Lista paginada de usuarios con datos de persona y roles asignados.

#### `PUT /api/v1/users/{id}/profile` (HU-SE-02)
Actualiza los datos personales de la persona sin alterar credenciales ni accesos de seguridad.
* **Payload Entrada:**
  ```json
  {
    "nombres": "Mateo",
    "apellidos": "Gómez Pérez",
    "telefono": "+573109876543",
    "fotoUrl": "https://sportflow.com/avatars/mateo.jpg"
  }
  ```
* **Respuestas:**
  - `200 OK`: Perfil actualizado.

---

### 2.3. Gestión de Roles y Permisos (RBAC)

#### `GET /api/v1/roles` (HU-SE-03 & HU-SE-04)
Consulta todos los roles registrados con su lista de permisos y cantidad de usuarios.
* **Cabecera:** `Authorization: Bearer <JWT>` (Requiere `ROLES:LISTAR`).
* **Respuestas:**
  - `200 OK`: Lista de roles del sistema.

#### `POST /api/v1/roles` (HU-SE-03)
Crea un nuevo rol de negocio.
* **Payload Entrada:**
  ```json
  {
    "code": "COORDINADOR_CANCHAS",
    "name": "Coordinador de Escenarios",
    "description": "Responsable de agendar y verificar el estado físico de los escenarios."
  }
  ```
* **Respuestas:**
  - `201 Created`: Rol creado con su UUID.
  - `409 Conflict`: Si el código de rol ya existe.

#### `DELETE /api/v1/roles/{id}` (HU-SE-03)
Elimina un rol existente, verificando que no tenga usuarios asignados ni sea rol de sistema.
* **Respuestas:**
  - `204 No Content`: Rol eliminado.
  - `400 Bad Request`: `{"code": "ROLE_HAS_ASSIGNED_USERS", "message": "No se puede eliminar el rol porque tiene usuarios asignados."}`

#### `PUT /api/v1/roles/{id}/permissions` (HU-SE-06)
Sincroniza y asigna una matriz de permisos a un rol.
* **Payload Entrada:**
  ```json
  {
    "permissionCodes": ["TORNEOS:VER", "TORNEOS:CREAR", "ESCENARIOS:VER"]
  }
  ```
* **Respuestas:**
  - `200 OK`: `{"mensaje": "Permisos actualizados para el rol."}`
