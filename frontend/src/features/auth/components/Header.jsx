import React from 'react';
import { Trophy, ShieldCheck, Users, Activity, LogOut } from 'lucide-react';

export default function Header({ user, onLogout, currentModule = 'security', onSelectModule }) {
  const navItems = [
    { id: 'security', label: 'Seguridad & IAM', icon: ShieldCheck, badge: 'IAM' },
    { id: 'sports', label: 'Gestión Deportiva', icon: Users, badge: 'M02' },
    { id: 'competitions', label: 'Competencias', icon: Trophy, badge: 'M03' },
  ];

  return (
    <header className="w-full border-b border-slate-800/80 bg-slate-950/80 backdrop-blur-md sticky top-0 z-40">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between gap-4">
        {/* Brand */}
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-500 via-teal-500 to-sky-500 flex items-center justify-center shadow-lg shadow-emerald-500/20">
            <Trophy className="w-5 h-5 text-slate-950 font-bold" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-heading font-extrabold text-xl tracking-tight text-white">SportFlow</span>
              <span className="text-[10px] uppercase font-bold tracking-widest px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
                PRO 2026
              </span>
            </div>
            <p className="text-[11px] text-slate-400 hidden md:block">Gestión Integral de Clubes y Torneos</p>
          </div>
        </div>

        {/* Navigation Tabs */}
        <nav className="flex items-center gap-1 sm:gap-2 p-1 bg-slate-900/90 rounded-2xl border border-slate-800 shadow-inner">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = currentModule === item.id;
            return (
              <button
                key={item.id}
                onClick={() => onSelectModule && onSelectModule(item.id)}
                className={`flex items-center gap-2 px-3 sm:px-4 py-1.5 rounded-xl text-xs font-semibold transition-all duration-200 ${
                  isActive
                    ? 'bg-gradient-to-r from-emerald-500 to-teal-500 text-slate-950 shadow-md shadow-emerald-500/20 font-bold'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800/60'
                }`}
              >
                <Icon className={`w-4 h-4 ${isActive ? 'text-slate-950' : 'text-slate-400'}`} />
                <span className="hidden sm:inline">{item.label}</span>
                <span className={`text-[9px] uppercase tracking-wider px-1.5 py-0.2 rounded font-mono ${
                  isActive ? 'bg-slate-950/20 text-slate-950' : 'bg-slate-800 text-slate-400'
                }`}>
                  {item.badge}
                </span>
              </button>
            );
          })}
        </nav>

        {/* Right Status & User */}
        <div className="flex items-center gap-3">
          <div className="hidden lg:flex items-center gap-2 px-3 py-1.5 rounded-full bg-slate-900/80 border border-slate-800 text-xs text-slate-300">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
            <span>API:</span>
            <span className="text-emerald-400 font-mono font-medium">Online (8080)</span>
          </div>

          {user ? (
            <div className="flex items-center gap-3">
              <div className="text-right hidden sm:block">
                <p className="text-xs font-semibold text-white leading-tight">{user.nombreCompleto}</p>
                <p className="text-[10px] text-slate-400 font-mono">{user.email}</p>
              </div>
              <button
                onClick={onLogout}
                title="Cerrar Sesión"
                className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold rounded-lg bg-rose-500/10 text-rose-400 border border-rose-500/20 hover:bg-rose-500/20 transition-all duration-200"
              >
                <LogOut className="w-3.5 h-3.5" />
                <span className="hidden md:inline">Salir</span>
              </button>
            </div>
          ) : (
            <button
              onClick={() => onSelectModule && onSelectModule('security')}
              className="px-3.5 py-1.5 text-xs font-semibold rounded-xl bg-emerald-500/15 text-emerald-400 border border-emerald-500/30 hover:bg-emerald-500/25 transition-all"
            >
              Iniciar Sesión
            </button>
          )}
        </div>
      </div>
    </header>
  );
}
