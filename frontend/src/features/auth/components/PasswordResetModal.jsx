import React, { useState } from 'react';
import { Key, X, Check, ArrowRight } from 'lucide-react';

export default function PasswordResetModal({ onRequestReset, onConfirmReset, onClose, loading }) {
  const [step, setStep] = useState('request'); // 'request' | 'confirm'
  const [email, setEmail] = useState('');
  const [token, setToken] = useState('');
  const [newPassword, setNewPassword] = useState('');

  const handleRequestSubmit = (e) => {
    e.preventDefault();
    onRequestReset(email, () => setStep('confirm'));
  };

  const handleConfirmSubmit = (e) => {
    e.preventDefault();
    onConfirmReset(token, newPassword);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="w-full max-w-md bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl p-6 relative animate-in fade-in zoom-in-95 duration-200">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 text-slate-400 hover:text-white p-1 rounded-lg hover:bg-white/10"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="flex items-center gap-3 mb-4">
          <div className="w-10 h-10 rounded-xl bg-purple-500/10 border border-purple-500/20 flex items-center justify-center text-purple-400">
            <Key className="w-5 h-5" />
          </div>
          <div>
            <h3 className="font-heading text-lg font-bold text-white">Recuperar Contraseña</h3>
            <p className="text-xs text-slate-400">HU-SE-09: Restablecimiento seguro</p>
          </div>
        </div>

        {step === 'request' ? (
          <form onSubmit={handleRequestSubmit} className="space-y-4">
            <p className="text-xs text-slate-300 leading-relaxed">
              Ingresa el correo asociado a tu cuenta tradicional. Generaremos un token con vigencia de 15 minutos.
            </p>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Correo Electrónico</label>
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@sportflow.com"
                className="w-full px-3 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-white text-xs outline-none focus:border-purple-500"
              />
            </div>

            <div className="flex gap-3 pt-2">
              <button
                type="button"
                onClick={() => setStep('confirm')}
                className="text-[11px] text-purple-400 hover:underline py-2"
              >
                Ya tengo un token
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white text-xs font-bold shadow-lg shadow-purple-500/20 disabled:opacity-50 flex items-center justify-center gap-2"
              >
                <span>Solicitar Enlace</span>
                <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </form>
        ) : (
          <form onSubmit={handleConfirmSubmit} className="space-y-4">
            <p className="text-xs text-slate-300 leading-relaxed">
              Ingresa el token recibido y define tu nueva contraseña segura.
            </p>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Token de Recuperación</label>
              <input
                type="text"
                required
                value={token}
                onChange={(e) => setToken(e.target.value)}
                placeholder="Pegar token aquí..."
                className="w-full px-3 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-white font-mono text-xs outline-none focus:border-purple-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Nueva Contraseña</label>
              <input
                type="password"
                required
                minLength={8}
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                placeholder="Mínimo 8 caracteres"
                className="w-full px-3 py-2.5 rounded-xl bg-slate-950 border border-slate-700 text-white text-xs outline-none focus:border-purple-500"
              />
            </div>

            <div className="flex gap-3 pt-2">
              <button
                type="button"
                onClick={() => setStep('request')}
                className="py-2.5 px-3 rounded-xl border border-slate-700 text-slate-300 text-xs"
              >
                Atrás
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 text-xs font-bold shadow-lg shadow-emerald-500/20 disabled:opacity-50 flex items-center justify-center gap-2"
              >
                <Check className="w-4 h-4" />
                <span>Restablecer y Cerrar Sesiones</span>
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
