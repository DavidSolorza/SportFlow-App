import React from 'react';
import { Trophy, Award } from 'lucide-react';

export default function StandingsTable({ standings = [], title = 'Tabla de Posiciones Oficial' }) {
  if (!standings || standings.length === 0) {
    return (
      <div className="bg-slate-950/40 border border-slate-800 rounded-xl p-6 text-center text-xs text-slate-500">
        No hay registros en la tabla de posiciones aún.
      </div>
    );
  }

  return (
    <div className="bg-slate-950/60 border border-slate-800 rounded-2xl overflow-hidden shadow-xl">
      <div className="px-5 py-3 border-b border-slate-800 bg-slate-900/50 flex items-center justify-between">
        <h4 className="font-heading text-sm font-bold text-white flex items-center gap-2">
          <Trophy className="w-4 h-4 text-amber-400" />
          <span>{title} (Criterios FIFA • HU-GC-08)</span>
        </h4>
        <span className="text-[11px] text-slate-400">Puntos: Victoria = 3 | Empate = 1</span>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-900/80 text-slate-400 text-[11px] uppercase tracking-wider font-semibold border-b border-slate-800">
            <tr>
              <th className="py-2.5 px-3 text-center">Pos</th>
              <th className="py-2.5 px-3">Equipo</th>
              <th className="py-2.5 px-2 text-center">PJ</th>
              <th className="py-2.5 px-2 text-center text-emerald-400">G</th>
              <th className="py-2.5 px-2 text-center text-amber-400">E</th>
              <th className="py-2.5 px-2 text-center text-rose-400">P</th>
              <th className="py-2.5 px-2 text-center">GF</th>
              <th className="py-2.5 px-2 text-center">GC</th>
              <th className="py-2.5 px-2 text-center font-bold">DG</th>
              <th className="py-2.5 px-3 text-center font-extrabold text-emerald-400">PTS</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/60 text-slate-200">
            {standings.map((entry, index) => {
              const isLeader = index === 0;
              const isTop = index < 2;

              return (
                <tr
                  key={entry.id || index}
                  className={`hover:bg-slate-800/30 transition ${isLeader ? 'bg-amber-500/5' : ''}`}
                >
                  <td className="py-2.5 px-3 text-center font-bold">
                    <span
                      className={`inline-flex items-center justify-center w-5 h-5 rounded-full text-[11px] ${
                        isLeader
                          ? 'bg-amber-500 text-slate-950 font-black shadow-sm shadow-amber-500/30'
                          : isTop
                          ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                          : 'text-slate-400'
                      }`}
                    >
                      {entry.posicion || index + 1}
                    </span>
                  </td>
                  <td className="py-2.5 px-3 font-semibold text-white flex items-center gap-2">
                    <span>{entry.nombreEquipo || 'Equipo'}</span>
                    {isLeader && <Award className="w-3.5 h-3.5 text-amber-400" />}
                  </td>
                  <td className="py-2.5 px-2 text-center font-mono text-slate-300">{entry.partidosJugados}</td>
                  <td className="py-2.5 px-2 text-center font-mono text-emerald-300 font-bold">{entry.victorias}</td>
                  <td className="py-2.5 px-2 text-center font-mono text-amber-300">{entry.empates}</td>
                  <td className="py-2.5 px-2 text-center font-mono text-rose-400">{entry.derrotas}</td>
                  <td className="py-2.5 px-2 text-center font-mono text-slate-400">{entry.golesFavor}</td>
                  <td className="py-2.5 px-2 text-center font-mono text-slate-400">{entry.golesContra}</td>
                  <td className="py-2.5 px-2 text-center font-mono font-bold">
                    <span className={entry.diferenciaGoles > 0 ? 'text-emerald-400' : entry.diferenciaGoles < 0 ? 'text-rose-400' : 'text-slate-400'}>
                      {entry.diferenciaGoles > 0 ? `+${entry.diferenciaGoles}` : entry.diferenciaGoles}
                    </span>
                  </td>
                  <td className="py-2.5 px-3 text-center font-mono font-black text-sm text-emerald-400 bg-emerald-500/5">
                    {entry.puntos}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}
