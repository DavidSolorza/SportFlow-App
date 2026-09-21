# 🧪 GUÍA DE EJECUCIÓN DE PRUEBAS POSTMAN - ENTREGA 2
**Proyecto:** SportFlow - Plataforma de Gestión de Eventos Deportivos  
**Versión:** 2.0.0 (Entrega 2)  
**Módulos Evaluados:** 
- Módulo 2: Gestión Deportiva (`HU-GD-01` a `HU-GD-08`)
- Módulo 3: Gestión de Competencias (`HU-GC-01` a `HU-GC-08`)

---

## 1. Archivos Requeridos
En la carpeta `postman/` del proyecto se encuentran los artefactos listos para importar:
1. **Colección:** [`SportFlow_E02_API.postman_collection.json`](file:///d:/escritorio/para%20entregar/proyecto%20universidad/back/postman/SportFlow_E02_API.postman_collection.json)
2. **Entorno:** [`SportFlow_Local.postman_environment.json`](file:///d:/escritorio/para%20entregar/proyecto%20universidad/back/postman/SportFlow_Local.postman_environment.json)

---

## 2. Preparación del Entorno en Postman
1. Abra **Postman**.
2. Haga clic en **Import** y seleccione los dos archivos mencionados anteriormente.
3. En la esquina superior derecha, seleccione el entorno activo: **SportFlow - Entorno Local**.
4. Inicie el backend localmente ejecutando:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
   *El servidor quedará escuchando en `http://localhost:8080/api/v1`.*

---

## 3. Flujo Automatizado de Pruebas (Runner o Ejecución Secuencial)

La colección está diseñada con scripts de pruebas (*Test Scripts*) en JavaScript que capturan dinámicamente los IDs generados y los propagan automáticamente entre las siguientes peticiones.

### Paso 0: Autenticación
- **Petición:** `0. Autenticación Previa / Login Administrador`
- **Credenciales por defecto:** `admin@sportflow.com` / `AdminPassword123!#`
- **Efecto:** Almacena el `jwt_token` en el entorno, autorizando todas las peticiones subsiguientes.

---

### Módulo 2: Gestión Deportiva (`HU-GD-01` a `HU-GD-08`)

| Orden | Nombre de la Petición | Historia | Descripción y Aserciones Automatizadas |
| :--- | :--- | :--- | :--- |
| **1** | `Crear Deporte Principal (Fútbol)` | **HU-GD-01** | Crea la disciplina raíz. Almacena `sport_id`. Verifica código HTTP `201 Created` y nombre `Fútbol`. |
| **2** | `Crear Subdeporte Recursivo (Fútbol Sala)` | **HU-GD-01** | Crea subdeporte referenciando a `sport_id`. Almacena `subsport_id`. Verifica recursividad (`deportePadreId`). |
| **3** | `Obtener Jerarquía Recursiva del Deporte` | **HU-GD-01** | `GET /sports/{{sport_id}}/hierarchy`. Verifica que `subdeportes` contenga la rama descendiente. |
| **4** | `Crear Equipo 1 (Águilas Doradas)` | **HU-GD-02** | Registra equipo con ciudad y categoría. Almacena `team1_id`. |
| **5** | `Crear Equipo 2 (Tiburones Rojos)` | **HU-GD-02** | Registra segundo equipo para la competencia. Almacena `team2_id`. |
| **6** | `Asociar Fútbol a Equipo 1` | **HU-GD-03** | Asocia la disciplina mediante relación M:N. Verifica código HTTP `200 OK`. |
| **7** | `Asociar Fútbol a Equipo 2` | **HU-GD-03** | Asocia la disciplina al segundo equipo. Verifica código HTTP `200 OK`. |
| **8** | `Registrar Jugador` | **HU-GD-04** | Registra jugador validando documento único y mayoría de edad deportiva. Almacena `player_id`. |
| **9** | `Fichar Jugador en Equipo 1` | **HU-GD-05** | Crea contrato en estado `ACTIVO` para el Equipo 1. Almacena `contract_id`. |
| **10** | `Traspasar Jugador a Equipo 2` | **HU-GD-05** | Registra nuevo contrato en Equipo 2. **Verifica que el contrato anterior se cierre con estado `FINALIZADO` y su motivo de transferencia.** |
| **11** | `Crear Habilidad en Catálogo` | **HU-GD-06** | Crea la habilidad técnica `Regate en velocidad`. Almacena `skill_id`. |
| **12** | `Asignar Habilidad a Jugador` | **HU-GD-07** | Asocia habilidad al jugador con nivel `AVANZADO`. Verifica relación M:N en `jugador_habilidades`. |
| **13** | `Consultar Perfil Deportivo Centralizado` | **HU-GD-08** | `GET /players/{{player_id}}/sports-profile`. Verifica la ficha integral: datos personales, equipo actual (`Tiburones Rojos`), historial de traspasos (2 contratos) y catálogo de habilidades. |

---

### Módulo 3: Gestión de Competencias (`HU-GC-01` a `HU-GC-08`)

| Orden | Nombre de la Petición | Historia | Descripción y Aserciones Automatizadas |
| :--- | :--- | :--- | :--- |
| **14** | `Crear Torneo Oficial` | **HU-GC-01** | Registra `Copa Metropolitana SportFlow 2026` con cupo para 16 equipos y validación cronológica de fechas. Almacena `tournament_id`. |
| **15** | `Inscribir Equipo 1 al Torneo` | **HU-GC-02** | Inscribe `Águilas Doradas FC`. Valida que practique el deporte del torneo y haya cupo disponible. Estado: `ACEPTADA`. |
| **16** | `Inscribir Equipo 2 al Torneo` | **HU-GC-02** | Inscribe `Tiburones Rojos`. Incrementa el contador de cupos del torneo. |
| **17** | `Crear Fase Regular (Grupos)` | **HU-GC-03** | Registra fase de tipo `GRUPOS`. Almacena `phase_id`. |
| **18** | `Crear Grupo A en la Fase` | **HU-GC-04** | Registra `Grupo A` dentro de `phase_id`. Almacena `group_id`. |
| **19** | `Asignar Equipo 1 al Grupo A` | **HU-GC-04** | Asigna Equipo 1 validando inscripción previa al torneo. |
| **20** | `Asignar Equipo 2 al Grupo A` | **HU-GC-04** | Asigna Equipo 2 validando que no esté duplicado en otro grupo de la misma fase. |
| **21** | `Crear Llave Eliminatoria (Playoff)` | **HU-GC-05** | Configura estructura de llaves directas (`TournamentBracket`). Almacena `bracket_id`. |
| **22** | `Programar Partido Oficial` | **HU-GC-06** | Programa encuentro entre Equipo 1 y Equipo 2 en fecha y escenario válido. Almacena `match_id`. |
| **23** | `Registrar Evento de Partido (Gol)` | **HU-GC-07** | Registra anotación en el minuto 35 para el jugador y equipo visitante. |
| **24** | `Registrar Resultado Oficial (2 - 1)` | **HU-GC-07** | Confirma resultado (2 goles local, 1 visitante). Pasa el estado del partido a `FINALIZADO` y confirma marcador. **Dispara el recálculo automático de la tabla de posiciones.** |
| **25** | `Consultar Tabla de Posiciones Recalculada` | **HU-GC-08** | Consulta tabla de posiciones del Grupo A. Verifica orden FIFA: Equipo 1 en 1ª posición con 3 pts (+1 DG) y Equipo 2 con 0 pts (-1 DG). |
| **26** | `Consultar Vista Integral del Desarrollo` | **HU-GC-08** | `GET /tournaments/{{tournament_id}}/development`. Retorna el árbol completo del torneo: fases, grupos, posiciones actualizadas, llaves y partidos disputados. |

---

## 4. Ejecución en Lote con Postman Collection Runner
1. En Postman, haga clic derecho sobre la colección **SportFlow - E02 API**.
2. Seleccione **Run collection**.
3. Asegúrese de que el entorno seleccionado sea **SportFlow - Entorno Local**.
4. Marque todas las peticiones (orden 0 al 26).
5. Presione **Run SportFlow - E02 API**.
6. Todas las aserciones deberán finalizar en verde (**100% Passed**).
