# 📊 MATRIZ DE TRAZABILIDAD DE REQUISITOS Y ARQUITECTURA (ENTREGA 2)
**Proyecto:** SportFlow - Plataforma de Gestión de Eventos Deportivos  
**Versión:** 2.0.0 (Entrega 2)  
**Fecha:** 2026-09-21  
**Responsable:** Agente 6 - Technical Writer & QA Specialist  
**Estado:** ✅ APROBADO / 100% IMPLEMENTADO Y VERIFICADO  

---

## 1. Alcance General de la Entrega 2
La **Segunda Entrega** de SportFlow comprende un total de **16 Historias de Usuario** distribuidas en dos módulos troncales:
- **Módulo 2: Gestión Deportiva (`HU-GD-01` a `HU-GD-08`)**
- **Módulo 3: Gestión de Competencias (`HU-GC-01` a `HU-GC-08`)**

Esta matriz asegura la correlación bidireccional entre la especificación funcional, el diseño de dominio, los casos de uso de negocio, las APIs REST, las pruebas automatizadas y las tablas en base de datos.

---

## 2. Matriz de Trazabilidad: Módulo 2 - Gestión Deportiva

| ID HU | Requerimiento / Descripción | Entidad de Dominio | Puerto de Repositorio | Caso de Uso (Application) | Endpoint REST (Controller) | Clase de Prueba Unitaria | Tablas Físicas BD |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **HU-GD-01** | Administrar disciplinas deportivas y jerarquía de subdeportes recursivos (`deporte_padre_id`). | `Sport` | `SportRepositoryPort` | `ManageSportUseCase` | `POST /api/v1/sports`<br>`GET /api/v1/sports/{id}/hierarchy` | `SportUseCaseTest` | `deportes` |
| **HU-GD-02** | Registrar y gestionar equipos asociados a un Club Deportivo. | `Team`, `Club` | `TeamRepositoryPort`, `ClubRepositoryPort` | `ManageTeamUseCase` | `POST /api/v1/teams`<br>`GET /api/v1/teams/{id}` | `TeamUseCaseTest` | `clubes`, `equipos` |
| **HU-GD-03** | Asociar múltiples disciplinas a un equipo (Relación M:N). | `Team` | `TeamRepositoryPort`, `SportRepositoryPort` | `ManageTeamUseCase` | `POST /api/v1/teams/{id}/sports/{sportId}`<br>`DELETE /api/v1/teams/{id}/sports/{sportId}` | `TeamUseCaseTest` | `equipo_deportes` |
| **HU-GD-04** | Registrar jugadores con validación de documento de identidad único y edad. | `Player` | `PlayerRepositoryPort` | `ManagePlayerUseCase` | `POST /api/v1/players`<br>`GET /api/v1/players/{id}` | `PlayerAndContractUseCaseTest` | `jugadores` |
| **HU-GD-05** | Fichajes de jugadores, historial inmutable de traspasos y cierre de contratos previos. | `PlayerContract` | `PlayerContractRepositoryPort` | `ManagePlayerContractUseCase` | `POST /api/v1/players/{id}/contracts`<br>`GET /api/v1/players/{id}/contracts` | `PlayerAndContractUseCaseTest` | `contratos_jugadores` |
| **HU-GD-06** | Catálogo centralizado de habilidades técnicas y tácticas deportivas. | `Skill` | `SkillRepositoryPort` | `ManageSkillUseCase` | `POST /api/v1/skills`<br>`GET /api/v1/skills` | `PlayerAndContractUseCaseTest` | `habilidades` |
| **HU-GD-07** | Asignación y evaluación de habilidades técnicas a jugadores (Relación M:N con nivel/observación). | `PlayerSkill` | `PlayerSkillRepositoryPort` | `ManageSkillUseCase` | `POST /api/v1/skills/players/{playerId}` | `PlayerAndContractUseCaseTest` | `jugador_habilidades` |
| **HU-GD-08** | Consulta integral y consolidada del perfil deportivo del jugador (Equipo, historial y habilidades). | `Player`, `PlayerContract`, `PlayerSkill` | `PlayerRepositoryPort`, `PlayerContractRepositoryPort`, `PlayerSkillRepositoryPort` | `GetPlayerSportsProfileUseCase` | `GET /api/v1/players/{id}/sports-profile` | `PlayerAndContractUseCaseTest` | `jugadores`, `contratos_jugadores`, `jugador_habilidades` |

---

## 3. Matriz de Trazabilidad: Módulo 3 - Gestión de Competencias

| ID HU | Requerimiento / Descripción | Entidad de Dominio | Puerto de Repositorio | Caso de Uso (Application) | Endpoint REST (Controller) | Clase de Prueba Unitaria | Tablas Físicas BD |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **HU-GC-01** | Creación y administración de torneos deportivos con control de cupos y fechas de inscripción. | `Tournament` | `TournamentRepositoryPort` | `ManageTournamentUseCase` | `POST /api/v1/tournaments`<br>`GET /api/v1/tournaments` | `TournamentUseCaseTest` | `torneos` |
| **HU-GC-02** | Inscripción oficial de equipos a torneos validando cupo máximo y correspondencia de disciplina. | `TournamentRegistration` | `TournamentRegistrationRepositoryPort` | `ManageTournamentUseCase` | `POST /api/v1/tournaments/{id}/register`<br>`GET /api/v1/tournaments/{id}/registrations` | `TournamentUseCaseTest` | `inscripciones_torneos` |
| **HU-GC-03** | Estructuración de fases y subfases jerárquicas recursivas (`fase_padre_id`). | `Phase` | `PhaseRepositoryPort` | `ManagePhaseUseCase` | `POST /api/v1/phases/tournament/{tournamentId}`<br>`GET /api/v1/phases/tournament/{tournamentId}` | `PhaseAndGroupUseCaseTest` | `fases` |
| **HU-GC-04** | Creación de grupos y asignación de equipos inscritos evitando duplicidad en la misma fase. | `CompetitionGroup` | `CompetitionGroupRepositoryPort` | `ManagePhaseUseCase` | `POST /api/v1/phases/{phaseId}/groups`<br>`POST /api/v1/phases/{phaseId}/groups/{groupId}/teams/{teamId}` | `PhaseAndGroupUseCaseTest` | `grupos`, `equipo_grupos` |
| **HU-GC-05** | Configuración de llaves eliminatorias directas (playoffs) y cruces dinámicos. | `TournamentBracket` | `TournamentBracketRepositoryPort` | `ManagePhaseUseCase` | `POST /api/v1/phases/{phaseId}/brackets`<br>`GET /api/v1/phases/{phaseId}/brackets` | `PhaseAndGroupUseCaseTest` | `llaves` |
| **HU-GC-06** | Programación oficial de partidos con validación de fechas, escenario y equipos no coincidentes. | `Match` | `MatchRepositoryPort` | `ManageMatchUseCase` | `POST /api/v1/matches`<br>`GET /api/v1/matches/{id}` | `MatchAndDevelopmentUseCaseTest` | `partidos` |
| **HU-GC-07** | Registro y confirmación de resultados oficiales y bitácora de eventos/goles con minuto y jugador. | `MatchResult`, `MatchEvent` | `MatchResultRepositoryPort`, `MatchEventRepositoryPort` | `ManageMatchUseCase` | `POST /api/v1/matches/{id}/result`<br>`POST /api/v1/matches/{id}/events` | `MatchAndDevelopmentUseCaseTest` | `resultados_partidos`, `eventos_partidos` |
| **HU-GC-08** | Motor determinista de cálculo de tablas de posiciones (criterios FIFA) y vista integral del desarrollo del torneo. | `StandingsEntry` | `StandingsRepositoryPort` | `TournamentDevelopmentUseCase` | `GET /api/v1/phases/{phaseId}/standings`<br>`GET /api/v1/tournaments/{id}/development` | `MatchAndDevelopmentUseCaseTest` | `clasificaciones` |

---

## 4. Cobertura de Pruebas Automatizadas
- **Total de pruebas unitarias y de integración:** 35 pruebas automatizadas ejecutadas en Maven.
- **Resultado de compilación y ejecución:** `BUILD SUCCESS` (0 fallos, 0 errores).
- **Aislamiento de puertos:** Los tests de casos de uso verifican exhaustivamente todas las reglas de negocio (prevenir doble asignación en grupos, validar deporte del equipo antes de inscribir a torneo, traspaso inmutable cerrando contrato anterior, cálculo exacto de puntos/diferencia de gol) utilizando Mockito sin acoplamiento a infraestructura.
