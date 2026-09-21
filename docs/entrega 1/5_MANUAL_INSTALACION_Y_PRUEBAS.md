# 🚀 Manual de Instalación, Configuración y Pruebas (Entrega 1)
**Proyecto:** SportFlow - Plataforma de Gestión de Eventos Deportivos  
**Módulo:** Seguridad y Control de Acceso (Entrega 1)  
**Autor:** Agente 6 - QA & Technical Writer  
**Versión:** 1.0.0  

---

## 1. Requisitos Previos del Entorno

Para ejecutar y validar la plataforma SportFlow en un entorno local, se requiere:

| Requisito | Versión Mínima | Versión Utilizada | Propósito |
| :--- | :--- | :--- | :--- |
| **Java Development Kit (JDK)** | Java 21+ | Java 25.0.1 LTS | Compilación y ejecución del Backend Spring Boot |
| **Node.js** | Node 18+ | Node 20+ / 22+ | Ejecución del servidor Vite y dependencias React |
| **NPM** | NPM 9+ | NPM 10+ | Gestor de paquetes del Frontend |
| **Navegador Web** | Moderno | Chrome / Edge / Firefox | Interacción con la interfaz web y flujos OAuth |

---

## 2. Configuración de Variables de Entorno

### 2.1. Backend (`src/main/resources/application.yml`)
El archivo ya cuenta con la configuración lista para desarrollo utilizando base de datos en memoria H2 (modo PostgreSQL) y las credenciales de OAuth integradas:

```yaml
sportflow:
  security:
    jwt:
      secret: "sportflow-super-secure-secret-key-that-must-be-at-least-256-bits-long-sportflow-2026"
      expiration-seconds: 86400
      issuer: "sportflow-auth-service"
    oauth:
      google:
        userinfo-url: "https://www.googleapis.com/oauth2/v3/userinfo"
      github:
        client-id: "${GITHUB_CLIENT_ID:Ov23lisRZCCGHj7BNSVt}"
        client-secret: "${GITHUB_CLIENT_SECRET:30b8d8aa00738962df4f80d218510e7c022b9ddf}"
        token-url: "https://github.com/login/oauth/access_token"
        userinfo-url: "https://api.github.com/user"
        emails-url: "https://api.github.com/user/emails"
```

### 2.2. Frontend (`frontend/.env`)
Ubicado en `d:\escritorio\para entregar\proyecto universidad\back\frontend\.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_GITHUB_CLIENT_ID=Ov23lisRZCCGHj7BNSVt
VITE_GOOGLE_CLIENT_ID=603064918591-evlh027lj39880khiu415ad508d18b06.apps.googleusercontent.com
```

---

## 3. Instrucciones de Arranque de los Servicios

### Paso 1: Levantar el Backend (Spring Boot)
Abre una terminal en la raíz del proyecto (`back/`):
```powershell
.\mvnw.cmd spring-boot:run
```
- **Disponibilidad:** `http://localhost:8080`
- **Consola de Base de Datos H2:** `http://localhost:8080/h2-console`
  - *JDBC URL:* `jdbc:h2:mem:sportflowdb`
  - *User:* `sa` | *Password:* *(en blanco)*

### Paso 2: Levantar el Frontend (React + Tailwind)
Abre una segunda terminal en la carpeta `frontend/`:
```powershell
cd frontend
npm run dev
```
- **Disponibilidad:** `http://localhost:5173/`

---

## 4. Credenciales Semilla y Datos de Prueba

Al iniciar el backend, el componente `SecurityDataSeeder.java` auto-provisiona el catálogo inicial del sistema:

| Cuenta / Rol | Correo Electrónico | Contraseña | Rol Asignado |
| :--- | :--- | :--- | :--- |
| **Super Administrador** | `admin@sportflow.com` | `AdminPassword123!#` | `ADMIN` (Acceso Total a Permisos) |
| **Atleta / Deportista** | *(Auto-provisionado al registrarse)* | *(Definida en registro)* | `ATHLETE` |
| **Organizador de Torneos** | *(Se asigna desde gestión)* | *(Definida en registro)* | `ORGANIZER` |

---

## 5. Guía de Pruebas Paso a Paso en la Interfaz Web

Abre tu navegador en **`http://localhost:5173/`**:

### Prueba 1: Autenticación Rápida con SuperAdmin
1. Pulsa el botón **`Admin: admin@sportflow.com`** para autocompletar credenciales.
2. Haz clic en **`Iniciar Sesión`**.
3. **Resultado esperado:**
   - La pantalla transiciona al **User Dashboard**.
   - Muestra el saludo: *"¡Bienvenido de vuelta, Administrador Principal!"*.
   - Se visualiza el Badge verde `ADMIN`.
   - Se despliega la matriz con los **14 permisos efectivos** del rol.
   - Permite inspeccionar y copiar el token JWT Bearer firmado.

### Prueba 2: Inicio de Sesión Federado con GitHub OAuth
1. En la pantalla de login, haz clic en el botón con el logo de **GitHub**.
2. El navegador redirigirá a la pasarela oficial: `https://github.com/login/oauth/authorize?client_id=Ov23lisRZCCGHj7BNSVt...`.
3. Inicia sesión en GitHub y autoriza la aplicación.
4. GitHub te devolverá a `http://localhost:5173/?code=...`.
5. El backend canjeará automáticamente el código por tu token de acceso nativo, consultará tu perfil de GitHub y te logueará inmediatamente en SportFlow.

### Prueba 3: Inicio de Sesión Federado con Google OAuth
1. En la pantalla de login, haz clic en el botón de **Google**.
2. El navegador redirigirá a la pasarela oficial de Google Accounts: `https://accounts.google.com/v3/signin/identifier?client_id=603064918591-...`.
3. Selecciona tu cuenta de Google y autoriza la aplicación.
4. Google devolverá el token en el fragmento `#access_token=ya29...`.
5. El frontend lo detectará y el backend autenticará la sesión extrayendo tu nombre y correo oficial.

### Prueba 4: Registro de Nuevo Usuario
1. En el formulario de inicio, haz clic en **`¿No tienes una cuenta? Regístrate aquí`**.
2. Diligencia los datos (Tipo de Documento, Número, Nombres, Apellidos, Teléfono, Correo y Contraseña).
3. Haz clic en **`Crear mi Cuenta`**.
4. **Resultado esperado:** Mensaje verde de confirmación y habilitación inmediata para iniciar sesión con tus credenciales.

### Prueba 5: Validación del Desafío 2FA
1. Pulsa en **`Probar/Simular Tokens OAuth2 Nativos`** o simula un login con 2FA habilitado.
2. Si un usuario tiene segundo factor activo, el sistema mostrará el **`TwoFactorModal`** solicitando los 6 dígitos OTP.
3. Al ingresar el código correcto, la sesión se desbloquea y se emite el JWT.

### Prueba 6: Cierre de Sesión (Logout)
1. Estando en el Dashboard, pulsa **`Cerrar Sesión`** (icono de salida).
2. El frontend consume `POST /api/v1/auth/logout`, revoca el token en el backend y limpia el `localStorage`.
3. El usuario regresa a la pantalla principal de login de manera limpia.

---

## 6. Evidencias de Pruebas Automatizadas (Backend)

Comando ejecutado para validación de regresión:
```powershell
.\mvnw.cmd test
```

### Resultados de Ejecución (10 de 10 Pruebas Aprobadas)
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.sportflow.features.security.AuthenticateOAuthUseCaseTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in AuthenticateOAuthUseCaseTest
[INFO] Running com.sportflow.features.security.RegisterAndAuthenticateTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 -- in RegisterAndAuthenticateTest
[INFO] Running com.sportflow.features.security.TwoFactorAndResetTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- in TwoFactorAndResetTest
[INFO] 
[INFO] Results:
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] -------------------------------------------------------
```
