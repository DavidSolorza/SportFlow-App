import { apiClient } from '../../../core/http/apiClient';
import { STORAGE_KEYS } from '../../../core/config/apiConfig';

/**
 * Servicio de infraestructura de autenticación (Agente 5).
 * Encapsula los contratos definidos en docs/3_api_spec.md
 */
export const authApiService = {
  /**
   * HU-SE-08: Login tradicional con credenciales
   */
  async login(email, password) {
    const response = await apiClient.post('/auth/login', { email, password });
    if (response.estado === 'AUTENTICADO' && response.tokenAcceso) {
      this.saveSession(response);
    }
    return response;
  },

  /**
   * HU-SE-10: Validación de desafío 2FA
   */
  async verify2FA(desafioToken, codigo) {
    const response = await apiClient.post('/auth/2fa/verify', { desafioToken, codigo });
    if (response.estado === 'AUTENTICADO' && response.tokenAcceso) {
      this.saveSession(response);
    }
    return response;
  },

  /**
   * HU-SE-08: Login mediante OAuth2 nativo (Google / GitHub)
   */
  async loginOAuth(provider, tokenProveedor) {
    const response = await apiClient.post(`/auth/oauth/${provider.toLowerCase()}`, {
      tokenProveedor,
      tipoToken: 'BEARER_TOKEN'
    });
    if (response.estado === 'AUTENTICADO' && response.tokenAcceso) {
      this.saveSession(response);
    }
    return response;
  },

  /**
   * HU-SE-07: Autoregistro de usuario con contraseña
   */
  async register(userData) {
    return await apiClient.post('/auth/register', userData);
  },

  /**
   * HU-SE-09: Solicitud de restablecimiento de clave
   */
  async requestPasswordReset(email) {
    return await apiClient.post('/auth/password-reset/request', { email });
  },

  /**
   * HU-SE-09: Confirmación de nueva contraseña
   */
  async confirmPasswordReset(token, nuevaPassword) {
    return await apiClient.post('/auth/password-reset/confirm', { token, nuevaPassword });
  },

  /**
   * HU-SE-08: Cierre de sesión y revocación en base de datos
   */
  async logout() {
    try {
      await apiClient.post('/auth/logout');
    } catch (e) {
      console.warn('Fallo al revocar token en servidor:', e);
    } finally {
      this.clearSession();
    }
  },

  saveSession(authResponse) {
    localStorage.setItem(STORAGE_KEYS.TOKEN, authResponse.tokenAcceso);
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(authResponse.usuario));
  },

  clearSession() {
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER);
  },

  getStoredToken() {
    return localStorage.getItem(STORAGE_KEYS.TOKEN);
  },

  getStoredUser() {
    const raw = localStorage.getItem(STORAGE_KEYS.USER);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  }
};
