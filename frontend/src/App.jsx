import React, { useState, useEffect } from 'react';
import Header from './features/auth/components/Header';
import LoginForm from './features/auth/components/LoginForm';
import SocialButtons from './features/auth/components/SocialButtons';
import TwoFactorModal from './features/auth/components/TwoFactorModal';
import RegisterModal from './features/auth/components/RegisterModal';
import OAuthTestModal from './features/auth/components/OAuthTestModal';
import PasswordResetModal from './features/auth/components/PasswordResetModal';
import UserDashboard from './features/auth/components/UserDashboard';
import StatusAlert from './features/auth/components/StatusAlert';
import { authApiService } from './features/auth/infrastructure/authApiService';
import { ShieldCheck, Flame, Trophy, Lock } from 'lucide-react';

export default function App() {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(false);
  const [alert, setAlert] = useState(null); // { type, message, details }

  // Modals state
  const [twoFactorData, setTwoFactorData] = useState(null); // { desafioToken }
  const [showRegister, setShowRegister] = useState(false);
  const [showOAuthTester, setShowOAuthTester] = useState(false);
  const [showReset, setShowReset] = useState(false);

  // Restore stored session on mount & check for OAuth callback code
  useEffect(() => {
    const storedUser = authApiService.getStoredUser();
    const storedToken = authApiService.getStoredToken();
    if (storedUser && storedToken) {
      setUser(storedUser);
      setToken(storedToken);
    }

    // Detect OAuth callback code from GitHub (e.g. ?code=XYZ)
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    if (code) {
      window.history.replaceState({}, document.title, window.location.pathname);
      setAlert({
        type: 'info',
        message: 'Procesando autorización de GitHub...'
      });
      handleOAuthLogin('GITHUB', code);
    }

    // Detect OAuth access token from Google (e.g. #access_token=XYZ)
    const hashParams = new URLSearchParams(window.location.hash.substring(1));
    const googleToken = hashParams.get('access_token');
    if (googleToken) {
      window.history.replaceState({}, document.title, window.location.pathname);
      setAlert({
        type: 'info',
        message: 'Procesando autorización de Google...'
      });
      handleOAuthLogin('GOOGLE', googleToken);
    }
  }, []);

  const handleLogin = async (email, password) => {
    setLoading(true);
    setAlert(null);
    try {
      const response = await authApiService.login(email, password);

      if (response.requiereSegundoFactor) {
        setTwoFactorData({ desafioToken: response.desafioToken });
        setAlert({
          type: 'info',
          message: 'Se requiere código 2FA para completar el acceso.'
        });
      } else {
        setUser(response.usuario);
        setToken(response.tokenAcceso);
        setAlert({
          type: 'success',
          message: `¡Bienvenido de vuelta, ${response.usuario.nombreCompleto}!`
        });
      }
    } catch (err) {
      setAlert({
        type: 'error',
        message: err.message,
        details: err.details
      });
    } finally {
      setLoading(false);
    }
  };

  const handleVerify2FA = async (desafioToken, codigo) => {
    setLoading(true);
    setAlert(null);
    try {
      const response = await authApiService.verify2FA(desafioToken, codigo);
      setTwoFactorData(null);
      setUser(response.usuario);
      setToken(response.tokenAcceso);
      setAlert({
        type: 'success',
        message: 'Segundo factor validado con éxito. Sesión iniciada.'
      });
    } catch (err) {
      setAlert({
        type: 'error',
        message: err.message
      });
    } finally {
      setLoading(false);
    }
  };

  const handleOAuthLogin = async (provider, tokenProveedor) => {
    setLoading(true);
    setAlert(null);
    try {
      const response = await authApiService.loginOAuth(provider, tokenProveedor);
      setShowOAuthTester(false);
      setUser(response.usuario);
      setToken(response.tokenAcceso);
      setAlert({
        type: 'success',
        message: `Sesión iniciada exitosamente con ${provider}.`
      });
    } catch (err) {
      setAlert({
        type: 'error',
        message: err.message
      });
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (formData) => {
    setLoading(true);
    setAlert(null);
    try {
      const response = await authApiService.register(formData);
      setShowRegister(false);
      setAlert({
        type: 'success',
        message: response.mensaje || 'Usuario registrado exitosamente. Ahora puedes iniciar sesión.'
      });
    } catch (err) {
      setAlert({
        type: 'error',
        message: err.message,
        details: err.details
      });
    } finally {
      setLoading(false);
    }
  };

  const handleRequestReset = async (email, onSuccess) => {
    setLoading(true);
    setAlert(null);
    try {
      const response = await authApiService.requestPasswordReset(email);
      setAlert({
        type: 'success',
        message: response.mensaje
      });
      if (onSuccess) onSuccess();
    } catch (err) {
      setAlert({
        type: 'error',
        message: err.message
      });
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmReset = async (tokenStr, nuevaPassword) => {
    setLoading(true);
    setAlert(null);
    try {
      const response = await authApiService.confirmPasswordReset(tokenStr, nuevaPassword);
      setShowReset(false);
      setAlert({
        type: 'success',
        message: response.mensaje
      });
    } catch (err) {
      setAlert({
        type: 'error',
        message: err.message
      });
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    await authApiService.logout();
    setUser(null);
    setToken(null);
    setAlert({
      type: 'info',
      message: 'Has cerrado sesión correctamente.'
    });
  };

  return (
    <div className="min-h-screen flex flex-col justify-between selection:bg-emerald-500 selection:text-slate-950">
      <Header user={user} onLogout={handleLogout} />

      <main className="flex-1 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full flex flex-col items-center justify-center">
        {/* Global Status Banner */}
        <div className="w-full max-w-md mb-6">
          <StatusAlert
            type={alert?.type}
            message={alert?.message}
            details={alert?.details}
            onClose={() => setAlert(null)}
          />
        </div>

        {user ? (
          <UserDashboard user={user} token={token} onLogout={handleLogout} />
        ) : (
          <div className="w-full max-w-md bg-slate-900/80 backdrop-blur-xl border border-slate-800 rounded-3xl p-6 sm:p-8 shadow-2xl relative overflow-hidden">
            {/* Top decorative gradient bar */}
            <div className="absolute top-0 left-0 right-0 h-1.5 bg-gradient-to-r from-emerald-500 via-teal-400 to-sky-500"></div>

            <div className="text-center mb-6">
              <div className="inline-flex items-center justify-center w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 mb-3 shadow-inner">
                <Lock className="w-6 h-6" />
              </div>
              <h2 className="font-heading text-2xl font-extrabold text-white tracking-tight">
                Iniciar Sesión
              </h2>
              <p className="text-xs text-slate-400 mt-1">
                Accede a la plataforma deportiva SportFlow
              </p>
            </div>

            {/* Social Logins */}
            <div className="mb-6">
              <SocialButtons
                onSelectProvider={(p) => {
                  if (p === 'GOOGLE') {
                    const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
                    if (googleClientId) {
                      window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${googleClientId}&redirect_uri=${encodeURIComponent(window.location.origin)}&response_type=token&scope=email%20profile`;
                    } else {
                      handleOAuthLogin('GOOGLE', 'google-sample-token-12345');
                    }
                  } else {
                    const githubClientId = import.meta.env.VITE_GITHUB_CLIENT_ID;
                    if (githubClientId) {
                      const redirectUri = window.location.origin;
                      window.location.href = `https://github.com/login/oauth/authorize?client_id=${githubClientId}&scope=read:user,user:email&redirect_uri=${encodeURIComponent(redirectUri)}`;
                    } else {
                      handleOAuthLogin('GITHUB', 'github-sample-token-12345');
                    }
                  }
                }}
                onOpenTester={() => setShowOAuthTester(true)}
                loading={loading}
              />

              <div className="relative my-6 text-center">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-slate-800"></div>
                </div>
                <span className="relative px-3 bg-slate-900 text-[11px] font-semibold uppercase tracking-wider text-slate-500">
                  O con correo electrónico
                </span>
              </div>
            </div>

            {/* Traditional Credentials Form */}
            <LoginForm
              onSubmit={handleLogin}
              onOpenRegister={() => setShowRegister(true)}
              onOpenReset={() => setShowReset(true)}
              loading={loading}
            />
          </div>
        )}
      </main>

      {/* Modals */}
      {twoFactorData && (
        <TwoFactorModal
          desafioToken={twoFactorData.desafioToken}
          onVerify={handleVerify2FA}
          onCancel={() => setTwoFactorData(null)}
          loading={loading}
        />
      )}

      {showRegister && (
        <RegisterModal
          onRegister={handleRegister}
          onClose={() => setShowRegister(false)}
          loading={loading}
        />
      )}

      {showOAuthTester && (
        <OAuthTestModal
          onTestOAuth={handleOAuthLogin}
          onClose={() => setShowOAuthTester(false)}
          loading={loading}
        />
      )}

      {showReset && (
        <PasswordResetModal
          onRequestReset={handleRequestReset}
          onConfirmReset={handleConfirmReset}
          onClose={() => setShowReset(false)}
          loading={loading}
        />
      )}

      <footer className="w-full border-t border-slate-900 bg-slate-950/60 py-4 text-center text-xs text-slate-500">
        <p>SportFlow &copy; 2026. Módulo de Seguridad & Control de Acceso (RBAC + OAuth2 Nativo).</p>
      </footer>
    </div>
  );
}
