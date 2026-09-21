import { apiClient } from '../../../core/http/apiClient';

/**
 * Servicio de infraestructura para Gestión Deportiva (Agente 5).
 * Consume los endpoints de /api/v1/sports, /api/v1/teams, /api/v1/players, /api/v1/skills
 */
export const sportsApiService = {
  // --- HU-GD-01: DISCIPLINAS Y SUBDEPORTES ---
  async createSport(data) {
    return await apiClient.post('/sports', data);
  },

  async listSports(soloActivos = null) {
    const query = soloActivos !== null ? `?soloActivos=${soloActivos}` : '';
    return await apiClient.get(`/sports${query}`);
  },

  async getSportById(id) {
    return await apiClient.get(`/sports/${id}`);
  },

  async getSportHierarchy(id) {
    return await apiClient.get(`/sports/${id}/hierarchy`);
  },

  // --- HU-GD-02 & HU-GD-03: EQUIPOS Y ASOCIACIÓN MULTIDISCIPLINAR ---
  async createTeam(data) {
    return await apiClient.post('/teams', data);
  },

  async listTeams() {
    return await apiClient.get('/teams');
  },

  async getTeamById(id) {
    return await apiClient.get(`/teams/${id}`);
  },

  async associateSportToTeam(teamId, sportId) {
    return await apiClient.post(`/teams/${teamId}/sports/${sportId}`);
  },

  async disassociateSportFromTeam(teamId, sportId) {
    return await apiClient.delete(`/teams/${teamId}/sports/${sportId}`);
  },

  // --- HU-GD-04: JUGADORES ---
  async createPlayer(data) {
    return await apiClient.post('/players', data);
  },

  async listPlayers() {
    return await apiClient.get('/players');
  },

  async getPlayerById(id) {
    return await apiClient.get(`/players/${id}`);
  },

  // --- HU-GD-05: FICHAJES E HISTORIAL INMUTABLE DE TRASPASOS ---
  async createContract(playerId, data) {
    return await apiClient.post(`/players/${playerId}/contracts`, data);
  },

  async listPlayerContracts(playerId) {
    return await apiClient.get(`/players/${playerId}/contracts`);
  },

  // --- HU-GD-06 & HU-GD-07: HABILIDADES Y ASIGNACIÓN ---
  async createSkill(data) {
    return await apiClient.post('/skills', data);
  },

  async listSkills() {
    return await apiClient.get('/skills');
  },

  async assignSkillToPlayer(playerId, data) {
    return await apiClient.post(`/skills/players/${playerId}`, data);
  },

  // --- HU-GD-08: PERFIL DEPORTIVO CENTRALIZADO ---
  async getPlayerSportsProfile(playerId) {
    return await apiClient.get(`/players/${playerId}/sports-profile`);
  }
};
