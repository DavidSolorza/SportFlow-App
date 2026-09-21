import React, { useState } from 'react';
import { X, ArrowRightLeft, Shield, Calendar, Hash, FileText } from 'lucide-react';

export default function NewTransferModal({ player, teams, onSubmit, onClose, loading }) {
  const [equipoId, setEquipoId] = useState('');
  const [fechaInicio, setFechaInicio] = useState(new Date().toISOString().split('T')[0]);
  const [fechaFin, setFechaFin] = useState('');
  const [numeroCamiseta, setNumeroCamiseta] = useState('');
  const [observaciones, setObservaciones] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!equipoId) return;

    onSubmit(player.id, {
      equipoId,
      fechaInicio,
      fechaFin: fechaFin || null,
      numeroCamiseta: numeroCamiseta ? parseInt(numeroCamiseta, 10) : null,
      observaciones: observaciones || 'Fichaje oficial de temporada'
    });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-in fade-in duration-200">
      <div className="w-full max-w-lg bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl overflow-hidden">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-slate-950/50">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-500/20 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
              <ArrowRightLeft className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-heading text-lg font-bold text-white">Gestionar Fichaje / Traspaso</h3>
              <p className="text-xs text-slate-400">Jugador: <strong className="text-white">{player.nombreCompleto}</strong> (HU-GD-05)</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div className="bg-emerald-950/30 border border-emerald-500/20 rounded-xl p-3 text-xs text-emerald-300">
            ℹ️ Si el jugador cuenta con un contrato activo con otro equipo, el sistema cerrará automáticamente el contrato previo con fecha de terminación oficial, garantizando la trazabilidad histórica inmutable.
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Equipo Destino *</label>
            <select
              value={equipoId}
              onChange={(e) => setEquipoId(e.target.value)}
              required
              className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white text-xs focus:outline-none focus:border-emerald-500 transition"
            >
              <option value="">-- Seleccionar Equipo --</option>
              {teams.map((t) => (
                <option key={t.id} value={t.id}>
                  {t.nombreDistintivo} ({t.ciudad} - {t.categoria})
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Fecha de Inicio *</label>
              <input
                type="date"
                value={fechaInicio}
                onChange={(e) => setFechaInicio(e.target.value)}
                required
                className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white text-xs focus:outline-none focus:border-emerald-500 transition"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Fecha de Fin (Opcional)</label>
              <input
                type="date"
                value={fechaFin}
                onChange={(e) => setFechaFin(e.target.value)}
                className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white text-xs focus:outline-none focus:border-emerald-500 transition"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Número de Camiseta / Dorsal</label>
            <input
              type="number"
              min="1"
              max="99"
              placeholder="Ej. 10"
              value={numeroCamiseta}
              onChange={(e) => setNumeroCamiseta(e.target.value)}
              className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white text-xs focus:outline-none focus:border-emerald-500 transition"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Observaciones / Motivo del Traspaso</label>
            <textarea
              rows="2"
              placeholder="Ej. Traspaso definitivo por acuerdo institucional..."
              value={observaciones}
              onChange={(e) => setObservaciones(e.target.value)}
              className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white text-xs focus:outline-none focus:border-emerald-500 transition"
            />
          </div>

          {/* Footer */}
          <div className="pt-2 flex items-center justify-end gap-3 border-t border-slate-800/80">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-bold transition"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading || !equipoId}
              className="px-4 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 text-xs font-extrabold transition shadow-lg shadow-emerald-500/20 disabled:opacity-50"
            >
              {loading ? 'Procesando...' : 'Confirmar Fichaje'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
