import React, { useState } from 'react';
import { ShieldCheck, UserCheck, Key, Copy, Check, LogOut, Lock, Trophy, Award } from 'lucide-react';

export default function UserDashboard({ user, token, onLogout }) {
  const [copied, setCopied] = useState(false);

  const handleCopyToken = () => {
    if (token) {
      navigator.clipboard.writeText(token);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <div className="w-full max-w-4xl mx-auto space-y-6 animate-in fade-in duration-300">
      {/* Welcome Banner */}
      <div className="rounded-2xl bg-gradient-to-r from-emerald-950/60 via-slate-900 to-sky-950/60 border border-emerald-500/20 p-6 shadow-xl relative overflow-hidden">
        <div className="absolute top-0 right-0 -mt-8 -mr-8 w-48 h-48 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none"></div>

        <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 relative z-10">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 rounded-2xl bg-gradient-to-tr from-emerald-500 to-teal-400 flex items-center justify-center text-slate-950 shadow-lg shadow-emerald-500/20">
              <UserCheck className="w-8 h-8 font-bold" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h2 className="font-heading text-xl font-bold text-white">{user.nombreCompleto}</h2>
                <span className="px-2 py-0.5 rounded-full text-[10px] font-extrabold uppercase tracking-wider bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                  {user.proveedorAuth || 'LOCAL'}
                </span>
              </div>
              <p className="text-xs text-slate-400 font-mono mt-0.5">{user.email}</p>
              <div className="flex items-center gap-2 mt-2">
                <span className="inline-flex items-center gap-1 text-[11px] text-emerald-400 font-medium">
                  <span className="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> Sesión Activa
                </span>
              </div>
            </div>
          </div>

          <button
            onClick={onLogout}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-rose-500/10 hover:bg-rose-500/20 text-rose-300 border border-rose-500/30 text-xs font-bold transition-all shadow-sm active:scale-95"
          >
            <LogOut className="w-4 h-4" />
            <span>Cerrar Sesión</span>
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Roles RBAC */}
        <div className="bg-slate-900/80 backdrop-blur-sm border border-slate-800 rounded-2xl p-5 shadow-lg">
          <div className="flex items-center gap-2 mb-3">
            <Trophy className="w-5 h-5 text-amber-400" />
            <h3 className="font-heading text-base font-bold text-white">Roles Asignados (RBAC)</h3>
          </div>
          <p className="text-xs text-slate-400 mb-4">
            Roles de usuario registrados en la base de datos de control de acceso:
          </p>

          <div className="flex flex-wrap gap-2">
            {user.roles && user.roles.length > 0 ? (
              user.roles.map((rol, i) => (
                <span
                  key={i}
                  className="px-3 py-1.5 rounded-xl text-xs font-bold bg-amber-500/10 border border-amber-500/30 text-amber-300 flex items-center gap-1.5 shadow-sm"
                >
                  <Award className="w-3.5 h-3.5" />
                  {rol}
                </span>
              ))
            ) : (
              <span className="text-xs text-slate-500">Sin roles específicos asignados</span>
            )}
          </div>
        </div>

        {/* Permisos Efectivos */}
        <div className="bg-slate-900/80 backdrop-blur-sm border border-slate-800 rounded-2xl p-5 shadow-lg">
          <div className="flex items-center gap-2 mb-3">
            <ShieldCheck className="w-5 h-5 text-sky-400" />
            <h3 className="font-heading text-base font-bold text-white">Permisos de la Matriz</h3>
          </div>
          <p className="text-xs text-slate-400 mb-4">
            Operaciones autorizadas calculadas dinámicamente según la matriz de permisos:
          </p>

          <div className="flex flex-wrap gap-1.5 max-h-40 overflow-y-auto pr-1">
            {user.permisos && user.permisos.length > 0 ? (
              user.permisos.map((p, i) => (
                <span
                  key={i}
                  className="px-2.5 py-1 rounded-lg text-[10px] font-mono font-semibold bg-sky-950/60 border border-sky-800/40 text-sky-300"
                >
                  {p}
                </span>
              ))
            ) : (
              <span className="text-xs text-slate-500">Sin permisos registrados</span>
            )}
          </div>
        </div>
      </div>

      {/* JWT Token Inspector */}
      <div className="bg-slate-900/80 backdrop-blur-sm border border-slate-800 rounded-2xl p-5 shadow-lg">
        <div className="flex items-center justify-between mb-3">
          <div className="flex items-center gap-2">
            <Key className="w-5 h-5 text-emerald-400" />
            <h3 className="font-heading text-base font-bold text-white">Token de Sesión JWT (Bearer)</h3>
          </div>
          <button
            onClick={handleCopyToken}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-xs font-medium text-slate-200 transition-colors"
          >
            {copied ? (
              <>
                <Check className="w-3.5 h-3.5 text-emerald-400" />
                <span className="text-emerald-400">Copiado</span>
              </>
            ) : (
              <>
                <Copy className="w-3.5 h-3.5" />
                <span>Copiar Token</span>
              </>
            )}
          </button>
        </div>

        <p className="text-xs text-slate-400 mb-3">
          Token criptográfico firmado con HMAC-SHA256 emitido por el backend Spring Boot. Contiene los claims del usuario y JTI para revocación:
        </p>

        <div className="p-3.5 rounded-xl bg-slate-950 border border-slate-800 font-mono text-xs text-emerald-400 break-all leading-relaxed max-h-32 overflow-y-auto select-all">
          {token}
        </div>
      </div>
    </div>
  );
}
