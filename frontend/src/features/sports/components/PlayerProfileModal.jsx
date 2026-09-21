import React from 'react';
import { X, User, Shield, Calendar, ArrowRight, Award, Trophy, Hash } from 'lucide-react';

export default function PlayerProfileModal({ profile, onClose }) {
  if (!profile) return null;
  const { jugador, equipoActual, historialTraspasos = [], habilidades = [] } = profile;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-in fade-in duration-200">
      <div className="w-full max-w-2xl bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-slate-950/50">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-500/20 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
              <User className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-heading text-lg font-bold text-white">Perfil Deportivo Centralizado</h3>
              <p className="text-xs text-slate-400">Ficha técnica y trazabilidad histórica (HU-GD-08)</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto space-y-6">
          {/* Jugador Card */}
          <div className="bg-slate-950/60 border border-slate-800/80 rounded-xl p-5 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
            <div>
              <div className="flex items-center gap-2">
                <h4 className="text-xl font-bold text-white">{jugador.nombreCompleto}</h4>
                <span className="px-2 py-0.5 rounded-full text-[10px] font-extrabold uppercase bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                  {jugador.estado}
                </span>
              </div>
              <p className="text-xs text-slate-400 mt-1">
                {jugador.tipoDocumento}: <span className="font-mono text-slate-300">{jugador.numeroIdentificacion}</span> • Posición: <span className="text-emerald-300 font-medium">{jugador.posicion || 'Polifuncional'}</span>
              </p>
              <p className="text-xs text-slate-500 mt-0.5">
                Nacimiento: {jugador.fechaNacimiento || 'N/D'}
              </p>
            </div>

            {equipoActual ? (
              <div className="bg-emerald-950/40 border border-emerald-500/30 rounded-xl p-3 text-right">
                <span className="text-[10px] uppercase font-bold tracking-wider text-emerald-400 block">Equipo Actual</span>
                <span className="text-base font-bold text-white block">{equipoActual.nombreEquipo}</span>
                <span className="text-xs text-emerald-300/80 inline-flex items-center gap-1 font-mono">
                  <Hash className="w-3 h-3" /> Dorsal #{equipoActual.numeroCamiseta || '-'}
                </span>
              </div>
            ) : (
              <div className="bg-slate-800/40 border border-slate-700/50 rounded-xl p-3 text-right">
                <span className="text-[10px] uppercase font-bold text-slate-400 block">Estado Actual</span>
                <span className="text-sm font-semibold text-amber-400 block">Agente Libre (Sin Contrato)</span>
              </div>
            )}
          </div>

          {/* Historial Inmutable de Traspasos (HU-GD-05) */}
          <div>
            <div className="flex items-center gap-2 mb-3">
              <Trophy className="w-4 h-4 text-emerald-400" />
              <h5 className="text-sm font-bold text-slate-200">Historial de Fichajes y Traspasos ({historialTraspasos.length})</h5>
            </div>
            {historialTraspasos.length === 0 ? (
              <p className="text-xs text-slate-500 italic">No registra contratos previos en la plataforma.</p>
            ) : (
              <div className="relative border-l-2 border-slate-800 ml-3 space-y-4 pl-4 py-1">
                {historialTraspasos.map((contrato) => (
                  <div key={contrato.id} className="relative group">
                    <div className={`absolute -left-[23px] top-1.5 w-3 h-3 rounded-full border-2 ${contrato.estado === 'ACTIVO' ? 'bg-emerald-500 border-emerald-300 ring-4 ring-emerald-500/20' : 'bg-slate-700 border-slate-600'}`}></div>
                    <div className="bg-slate-950/40 border border-slate-800/80 rounded-xl p-3 text-xs">
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-white text-sm">{contrato.nombreEquipo}</span>
                        <span className={`px-2 py-0.5 rounded text-[10px] font-extrabold uppercase ${contrato.estado === 'ACTIVO' ? 'bg-emerald-500/20 text-emerald-400' : 'bg-slate-800 text-slate-400'}`}>
                          {contrato.estado}
                        </span>
                      </div>
                      <div className="flex items-center gap-3 text-slate-400 mt-1">
                        <span>Desde: <strong className="text-slate-300 font-mono">{contrato.fechaInicio}</strong></span>
                        <span>Hasta: <strong className="text-slate-300 font-mono">{contrato.fechaFin || 'Vigente'}</strong></span>
                        {contrato.numeroCamiseta && <span>Camiseta: #{contrato.numeroCamiseta}</span>}
                      </div>
                      {contrato.observaciones && (
                        <p className="text-[11px] text-slate-500 mt-1.5 border-t border-slate-900 pt-1">
                          Observación: {contrato.observaciones}
                        </p>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Habilidades Técnicas y Evaluación (HU-GD-06, HU-GD-07) */}
          <div>
            <div className="flex items-center gap-2 mb-3">
              <Award className="w-4 h-4 text-emerald-400" />
              <h5 className="text-sm font-bold text-slate-200">Habilidades Técnicas y Tácticas ({habilidades.length})</h5>
            </div>
            {habilidades.length === 0 ? (
              <p className="text-xs text-slate-500 italic">No registra habilidades asignadas aún.</p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {habilidades.map((hab, idx) => (
                  <div key={idx} className="bg-slate-950/40 border border-slate-800 rounded-xl p-3 text-xs">
                    <div className="flex items-center justify-between mb-1">
                      <span className="font-bold text-white">{hab.nombreHabilidad}</span>
                      <span className="px-2 py-0.5 rounded text-[10px] font-extrabold uppercase bg-emerald-500/20 text-emerald-300 border border-emerald-500/30">
                        {hab.nivel}
                      </span>
                    </div>
                    {hab.observacion && (
                      <p className="text-[11px] text-slate-400 mt-1">{hab.observacion}</p>
                    )}
                    <span className="text-[10px] text-slate-500 block mt-2 font-mono">Evaluado: {hab.fechaRegistro}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-slate-800 bg-slate-950/50 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-bold transition"
          >
            Cerrar Ficha
          </button>
        </div>
      </div>
    </div>
  );
}
