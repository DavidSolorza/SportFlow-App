import React, { useState } from 'react';
import { KeyRound, X, Send, ShieldAlert, Cpu } from 'lucide-react';

export default function OAuthTestModal({ onTestOAuth, onClose, loading }) {
  const [provider, setProvider] = useState('GOOGLE');
  const [token, setToken] = useState('demo-token-sportflow-oauth');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (token.trim()) {
      onTestOAuth(provider, token.trim());
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="w-full max-w-lg bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl p-6 relative animate-in fade-in zoom-in-95 duration-200">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-slate-400 hover:text-white p-1 rounded-lg hover:bg-white/10"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="flex items-center gap-3 mb-4">
          <div className="w-10 h-10 rounded-xl bg-sky-500/10 border border-sky-500/20 flex items-center justify-center text-sky-400">
            <Cpu className="w-5 h-5" />
          </div>
          <div>
            <h3 className="font-heading text-lg font-bold text-white">Probador de OAuth2 Nativo</h3>
            <p className="text-xs text-slate-400">Endpoint: /api/v1/auth/oauth/{'{provider}'}</p>
          </div>
        </div>

        {/* Governance banner */}
        <div className="mb-4 p-3 rounded-xl bg-slate-950 border border-slate-800 text-[11px] text-slate-300 leading-relaxed">
          <span className="font-bold text-emerald-400">Gobernanza Cero SDKs:</span> El backend de SportFlow no utiliza SDKs de Google ni de GitHub. Consume directamente las APIs REST de los proveedores mediante su cliente HTTP nativo inyectando <code className="text-sky-300">Authorization: Bearer</code>.
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">Proveedor OAuth</label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setProvider('GOOGLE')}
                className={`py-2 px-3 rounded-xl border text-xs font-bold transition-all ${
                  provider === 'GOOGLE'
                    ? 'bg-red-500/10 border-red-500/50 text-red-300 shadow-sm'
                    : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-white'
                }`}
              >
                Google OAuth
              </button>
              <button
                type="button"
                onClick={() => setProvider('GITHUB')}
                className={`py-2 px-3 rounded-xl border text-xs font-bold transition-all ${
                  provider === 'GITHUB'
                    ? 'bg-slate-700/50 border-slate-500 text-white shadow-sm'
                    : 'bg-slate-950 border-slate-800 text-slate-400 hover:text-white'
                }`}
              >
                GitHub OAuth
              </button>
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1.5">
              Token del Proveedor (ID Token o Access Token)
            </label>
            <textarea
              rows={3}
              required
              value={token}
              onChange={(e) => setToken(e.target.value)}
              placeholder="Ingresa un token de Google o GitHub..."
              className="w-full p-3 rounded-xl bg-slate-950 border border-slate-700 text-white font-mono text-xs outline-none focus:border-sky-500 resize-none"
            />
            <p className="text-[11px] text-slate-400 mt-1">
              Puedes pegar un token real obtenido de Google OAuth Playground o ingresar cualquier token para comprobar la verificación contra el proveedor.
            </p>
          </div>

          <div className="flex gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2.5 rounded-xl border border-slate-700 hover:bg-slate-800 text-slate-300 text-xs font-semibold"
            >
              Cerrar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 py-2.5 rounded-xl bg-sky-500 hover:bg-sky-400 text-slate-950 text-xs font-bold shadow-lg shadow-sky-500/20 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              {loading ? (
                <div className="w-4 h-4 border-2 border-slate-950 border-t-transparent rounded-full animate-spin"></div>
              ) : (
                <>
                  <Send className="w-4 h-4" />
                  <span>Enviar a API Backend</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
