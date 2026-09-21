import { apiClient } from '../../../core/http/apiClient';

/**
 * Servicio de infraestructura para Gestión de Competencias (Agente 5).
 * Consume los endpoints de /api/v1/tournaments, /api/v1/phases, /api/v1/matches
 */
export const competitionsApiService = {
  // --- HU-GC-01: TORNEOS ---
  async createTournament(data) {
    return await apiClient.post('/tournaments', data);
  },

  async listTournaments(sportId = null) {
    const query = sportId ? `?sportId=${sportId}` : '';
    return await apiClient.get(`/tournaments${query}`);
  },

  async getTournamentById(id) {
    return await apiClient.get(`/tournaments/${id}`);
  },

  // --- HU-GC-02: INSCRIPCIONES ---
  async registerTeam(tournamentId, data) {
    return await apiClient.post(`/tournaments/${tournamentId}/register`, data);
  },

  async getTournamentRegistrations(tournamentId) {
    return await apiClient.get(`/tournaments/${tournamentId}/registrations`);
  },

  // --- HU-GC-08: VISTA INTEGRAL DEL DESARROLLO DEL TORNEO ---
  async getTournamentDevelopment(tournamentId) {
    return await apiClient.get(`/tournaments/${tournamentId}/development`);
  },

  // --- HU-GC-03: FASES ---
  async createPhase(tournamentId, data) {
    return await apiClient.post(`/phases/tournament/${tournamentId}`, data);
  },

  async getPhasesByTournament(tournamentId) {
    return await apiClient.get(`/phases/tournament/${tournamentId}`);
  },

  // --- HU-GC-04: GRUPOS ---
  async createGroup(phaseId, data) {
    return await apiClient.post(`/phases/${phaseId}/groups`, data);
  },

  async getGroupsByPhase(phaseId) {
    return await apiClient.get(`/phases/${phaseId}/groups`);
  },

  async assignTeamToGroup(phaseId, groupId, teamId) {
    return await apiClient.post(`/phases/${phaseId}/groups/${groupId}/teams/${teamId}`);
  },

  // --- HU-GC-05: LLAVES (PLAYOFFS) ---
  async createBracket(phaseId, data) {
    return await apiClient.post(`/phases/${phaseId}/brackets`, data);
  },

  async getBracketsByPhase(phaseId) {
    return await apiClient.get(`/phases/${phaseId}/brackets`);
  },

  // --- HU-GC-06: PARTIDOS ---
  async scheduleMatch(data) {
    return await apiClient.post('/matches', data);
  },

  async getMatchById(id) {
    return await apiClient.get(`/matches/${id}`);
  },

  async getMatchesByPhase(phaseId) {
    return await apiClient.get(`/phases/${phaseId}/matches`);
  },

  // --- HU-GC-07: RESULTADOS Y EVENTOS ---
  async recordMatchResult(matchId, data) {
    return await apiClient.post(`/matches/${matchId}/result`, data);
  },

  async recordMatchEvent(matchId, data) {
    return await apiClient.post(`/matches/${matchId}/events`, data);
  },

  async getMatchEvents(matchId) {
    return await apiClient.get(`/matches/${matchId}/events`);
  },

  // --- HU-GC-08: TABLA DE POSICIONES ---
  async getStandings(phaseId, groupId = null) {
    const query = groupId ? `?groupId=${groupId}` : '';
    return await apiClient.get(`/phases/${phaseId}/standings${query}`);
  },

  async calculateGroupStandings(phaseId, groupId) {
    return await apiClient.post(`/phases/${phaseId}/groups/${groupId}/calculate-standings`);
  }
};
