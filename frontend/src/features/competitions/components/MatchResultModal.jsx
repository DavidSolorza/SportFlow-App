import React, { useState } from 'react';
import { X, Trophy, CheckCircle, Plus, Flag, Clock } from 'lucide-react';

export default function MatchResultModal({ match, players = [], onSaveResult, onAddEvent, onClose, loading }) {
  if (!match) return null;

  // Result state
  const [golesLocal, setGolesLocal] = useState(match.resultado ? match.resultado.golesLocal : 0);
  const [golesVisitante, setGolesVisitante] = useState(match.resultado ? match.resultado.golesVisitante : 0);
  const [confirmadoPor, setConfirmadoPor] = useState('Comisario Oficial');
  const [observaciones, setObservaciones] = useState('');

  // Event state
  const [showEventForm, setShowEventForm] = useState(false);
  const [eventType, setEventType] = useState('GOL');
  const [minute, setMinute] = useState(1);
  const [selectedTeamId, setSelectedTeamId] = useState(match.equipoLocalId || '');
  const [selectedPlayerId, setSelectedPlayerId] = useState('');
  const [eventDesc, setEventDesc] = useState('');

  const handleResultSubmit = (e) => {
    e.preventDefault();
    onSaveResult(match.id, {
      golesLocal: parseInt(golesLocal, 10),
      golesVisitante: parseInt(golesVisitante, 10),
      confirmadoPor,
      observaciones: observaciones || 'Marcador confirmado sin novedades'
    });
  };

  const handleEventSubmit = (e) => {
    e.preventDefault();
    if (!selectedTeamId) return;

    onAddEvent(match.id, {
      equipoId: selectedTeamId,
      jugadorId: selectedPlayerId || null,
      tipoEvento: eventType,
      minuto: parseInt(minute, 10),
      descripcion: eventDesc || `${eventType} en el minuto ${minute}`
    });
    setEventDesc('');
    setShowEventForm(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-in fade-in duration-200">
      <div className="w-full max-w-xl bg-slate-900 border border-slate-800 rounded-2xl shadow-2xl overflow-hidden max-h-[90vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-slate-950/50">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-emerald-500/20 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
              <Trophy className="w-5 h-5" />
            </div>
            <div>
              <h3 className="font-heading text-lg font-bold text-white">Acta Oficial del Partido</h3>
              <p className="text-xs text-slate-400">Marcador definitivo y bitácora de eventos (HU-GC-07)</p>
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
          {/* Match Score Board */}
          <div className="bg-slate-950/70 border border-slate-800 rounded-2xl p-5 text-center">
            <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-widest block mb-3">
              {match.escenario || 'Escenario Principal'} • {new Date(match.fechaHoraProgramada).toLocaleDateString()}
            </span>

            <div className="grid grid-cols-5 items-center gap-2">
              <div className="col-span-2 text-right">
                <span className="text-base font-bold text-white block">{match.nombreEquipoLocal}</span>
                <span className="text-[10px] text-slate-400 uppercase font-semibold">Local</span>
              </div>

              <div className="col-span-1 flex items-center justify-center gap-2">
                <input
                  type="number"
                  min="0"
                  max="50"
                  disabled={match.resultadoConfirmado}
                  value={golesLocal}
                  onChange={(e) => setGolesLocal(e.target.value)}
                  className="w-12 h-12 text-center text-xl font-black bg-slate-900 border border-slate-700 text-white rounded-xl focus:border-emerald-500 disabled:opacity-75"
                />
                <span className="text-slate-500 font-bold text-lg">-</span>
                <input
                  type="number"
                  min="0"
                  max="50"
                  disabled={match.resultadoConfirmado}
                  value={golesVisitante}
                  onChange={(e) => setGolesVisitante(e.target.value)}
                  className="w-12 h-12 text-center text-xl font-black bg-slate-900 border border-slate-700 text-white rounded-xl focus:border-emerald-500 disabled:opacity-75"
                />
              </div>

              <div className="col-span-2 text-left">
                <span className="text-base font-bold text-white block">{match.nombreEquipoVisitante}</span>
                <span className="text-[10px] text-slate-400 uppercase font-semibold">Visitante</span>
              </div>
            </div>

            {match.resultadoConfirmado && (
              <span className="inline-flex items-center gap-1 text-[11px] font-bold text-emerald-400 bg-emerald-500/10 px-3 py-1 rounded-full border border-emerald-500/20 mt-4">
                <CheckCircle className="w-3.5 h-3.5" /> Marcador Confirmado Oficialmente
              </span>
            )}
          </div>

          {/* Form Confirm Score */}
          {!match.resultadoConfirmado && (
            <form onSubmit={handleResultSubmit} className="space-y-3 bg-slate-950/40 border border-slate-800/80 rounded-xl p-4 text-xs">
              <h4 className="font-bold text-white text-xs">Confirmar Marcador Final</h4>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Confirmado Por *</label>
                  <input
                    type="text"
                    value={confirmadoPor}
                    onChange={(e) => setConfirmadoPor(e.target.value)}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Observaciones</label>
                  <input
                    type="text"
                    placeholder="Incidencias o acta arbitral..."
                    value={observaciones}
                    onChange={(e) => setObservaciones(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
              </div>
              <button
                type="submit"
                disabled={loading}
                className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold transition shadow-lg shadow-emerald-500/20 disabled:opacity-50"
              >
                {loading ? 'Procesando...' : 'Confirmar Resultado & Recalcular Posiciones'}
              </button>
            </form>
          )}

          {/* Event Logging */}
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <h4 className="text-xs font-bold text-white flex items-center gap-1.5">
                <Flag className="w-3.5 h-3.5 text-emerald-400" />
                <span>Bitácora de Eventos por Minuto</span>
              </h4>
              <button
                type="button"
                onClick={() => setShowEventForm(!showEventForm)}
                className="px-2.5 py-1 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold flex items-center gap-1 transition"
              >
                <Plus className="w-3.5 h-3.5 text-emerald-400" />
                <span>{showEventForm ? 'Cancelar Evento' : 'Registrar Evento'}</span>
              </button>
            </div>

            {showEventForm && (
              <form onSubmit={handleEventSubmit} className="bg-slate-950 border border-slate-800 rounded-xl p-4 space-y-3 text-xs">
                <div className="grid grid-cols-3 gap-2">
                  <div>
                    <label className="block text-slate-300 font-semibold mb-1">Tipo de Evento</label>
                    <select
                      value={eventType}
                      onChange={(e) => setEventType(e.target.value)}
                      className="w-full px-2 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                    >
                      <option value="GOL">⚽ Gol</option>
                      <option value="TARJETA_AMARILLA">🟨 Tarjeta Amarilla</option>
                      <option value="TARJETA_ROJA">🟥 Tarjeta Roja</option>
                      <option value="SUSTITUCION">🔄 Sustitución</option>
                      <option value="PENAL">🎯 Tiro Penal</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-slate-300 font-semibold mb-1">Minuto (0-150)</label>
                    <input
                      type="number"
                      min="1"
                      max="150"
                      value={minute}
                      onChange={(e) => setMinute(e.target.value)}
                      required
                      className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white focus:outline-none focus:border-emerald-500 font-mono"
                    />
                  </div>
                  <div>
                    <label className="block text-slate-300 font-semibold mb-1">Equipo</label>
                    <select
                      value={selectedTeamId}
                      onChange={(e) => setSelectedTeamId(e.target.value)}
                      className="w-full px-2 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                    >
                      <option value={match.equipoLocalId}>{match.nombreEquipoLocal}</option>
                      <option value={match.equipoVisitanteId}>{match.nombreEquipoVisitante}</option>
                    </select>
                  </div>
                </div>

                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Jugador Protagonista (Opcional)</label>
                  <select
                    value={selectedPlayerId}
                    onChange={(e) => setSelectedPlayerId(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="">-- No especificar / Autogol --</option>
                    {players.map((p) => (
                      <option key={p.id} value={p.id}>{p.nombreCompleto}</option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Descripción del Evento</label>
                  <input
                    type="text"
                    placeholder="Remate cruzado al palo izquierdo..."
                    value={eventDesc}
                    onChange={(e) => setEventDesc(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full py-2 rounded-xl bg-emerald-500/20 hover:bg-emerald-500/30 text-emerald-300 border border-emerald-500/30 font-bold transition"
                >
                  Guardar Evento en Acta
                </button>
              </form>
            )}
          </div>
        </div>

        {/* Footer */}
        <div className="px-6 py-4 border-t border-slate-800 bg-slate-950/50 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-bold transition"
          >
            Cerrar Acta
          </button>
        </div>
      </div>
    </div>
  );
}
