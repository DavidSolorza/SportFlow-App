import React, { useState } from 'react';
import { Mail, Lock, LogIn, UserPlus, Sparkles, Key } from 'lucide-react';

export default function LoginForm({ onSubmit, onOpenRegister, onOpenReset, loading }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(email, password);
  };

  const handleFillAdmin = () => {
    setEmail('admin@sportflow.com');
    setPassword('AdminPassword123!#');
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      {/* Autofill test helper */}
      <div className="flex items-center justify-between px-3 py-2 bg-emerald-950/30 border border-emerald-800/40 rounded-xl text-xs">
        <div className="flex items-center gap-2 text-emerald-300">
          <Sparkles className="w-4 h-4 text-emerald-400" />
          <span>Acceso rápido de prueba:</span>
        </div>
        <button
          type="button"
          onClick={handleFillAdmin}
          className="px-2 py-1 bg-emerald-500/20 hover:bg-emerald-500/30 text-emerald-300 rounded font-medium transition-colors"
        >
          Autocompletar SuperAdmin
        </button>
      </div>

      <div>
        <label className="block text-xs font-semibold text-slate-300 mb-1.5">Correo Electrónico</label>
        <div className="relative">
          <Mail className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="ejemplo@sportflow.com"
            className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-slate-900 border border-slate-700/80 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 outline-none text-white text-sm placeholder-slate-500 transition-all"
          />
        </div>
      </div>

      <div>
        <div className="flex items-center justify-between mb-1.5">
          <label className="text-xs font-semibold text-slate-300">Contraseña</label>
          <button
            type="button"
            onClick={onOpenReset}
            className="text-[11px] text-sky-400 hover:text-sky-300 hover:underline"
          >
            ¿Olvidaste tu contraseña?
          </button>
        </div>
        <div className="relative">
          <Lock className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••••••"
            className="w-full pl-10 pr-4 py-2.5 rounded-xl bg-slate-900 border border-slate-700/80 focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 outline-none text-white text-sm placeholder-slate-500 transition-all"
          />
        </div>
      </div>

      <button
        type="submit"
        disabled={loading}
        className="w-full flex items-center justify-center gap-2 py-3 px-4 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-400 hover:to-teal-500 text-slate-950 font-bold text-sm shadow-lg shadow-emerald-500/20 active:scale-[0.99] transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {loading ? (
          <div className="w-5 h-5 border-2 border-slate-950 border-t-transparent rounded-full animate-spin"></div>
        ) : (
          <>
            <LogIn className="w-4 h-4" />
            <span>Iniciar Sesión</span>
          </>
        )}
      </button>

      <div className="text-center pt-2">
        <p className="text-xs text-slate-400">
          ¿No tienes una cuenta?{' '}
          <button
            type="button"
            onClick={onOpenRegister}
            className="font-semibold text-emerald-400 hover:text-emerald-300 hover:underline inline-flex items-center gap-1"
          >
            <UserPlus className="w-3.5 h-3.5" />
            Registrarme
          </button>
        </p>
      </div>
    </form>
  );
}
