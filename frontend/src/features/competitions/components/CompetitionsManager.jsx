import React, { useState, useEffect } from 'react';
import {
  Trophy, Layers, Calendar, Flag, Award, Plus, CheckCircle, AlertTriangle, RefreshCw, Users, Shield, ArrowRight
} from 'lucide-react';
import { competitionsApiService } from '../infrastructure/competitionsApiService';
import { sportsApiService } from '../../sports/infrastructure/sportsApiService';
import StandingsTable from './StandingsTable';
import MatchResultModal from './MatchResultModal';

export default function CompetitionsManager() {
  const [activeTab, setActiveTab] = useState('tournaments'); // tournaments | phases | matches | development
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState(null);

  // Data states
  const [tournaments, setTournaments] = useState([]);
  const [sports, setSports] = useState([]);
  const [teams, setTeams] = useState([]);
  const [players, setPlayers] = useState([]);
  const [selectedTournamentId, setSelectedTournamentId] = useState('');
  const [phases, setPhases] = useState([]);
  const [selectedPhaseId, setSelectedPhaseId] = useState('');
  const [groups, setGroups] = useState([]);
  const [selectedGroupId, setSelectedGroupId] = useState('');
  const [matches, setMatches] = useState([]);
  const [standings, setStandings] = useState([]);
  const [tournamentDevelopment, setTournamentDevelopment] = useState(null);

  // Match Modal
  const [selectedMatchForActa, setSelectedMatchForActa] = useState(null);

  // Forms
  const [newTournament, setNewTournament] = useState({
    nombre: '', descripcion: '', deporteId: '',
    fechaInicio: '', fechaFin: '', fechaCierreInscripcion: '', cupoEquipos: 16
  });
  const [newRegistration, setNewRegistration] = useState({ equipoId: '', observaciones: '' });
  const [newPhase, setNewPhase] = useState({ nombre: '', tipo: 'GRUPOS', orden: 1, fasePadreId: '' });
  const [newGroup, setNewGroup] = useState({ nombre: '', orden: 1 });
  const [assignGroupTeam, setAssignGroupTeam] = useState({ groupId: '', teamId: '' });
  const [newBracket, setNewBracket] = useState({ nombre: '', ronda: 1, orden: 1 });
  const [newMatch, setNewMatch] = useState({
    equipoLocalId: '', equipoVisitanteId: '', fechaHoraProgramada: '', escenario: ''
  });

  useEffect(() => {
    loadInitialData();
  }, []);

  useEffect(() => {
    if (selectedTournamentId) {
      loadTournamentDetails(selectedTournamentId);
    }
  }, [selectedTournamentId]);

  useEffect(() => {
    if (selectedPhaseId) {
      loadPhaseDetails(selectedPhaseId);
    }
  }, [selectedPhaseId]);

  const showNotification = (type, text) => {
    setNotification({ type, text });
    setTimeout(() => setNotification(null), 4000);
  };

  const loadInitialData = async () => {
    setLoading(true);
    try {
      const [tournamentsData, sportsData, teamsData, playersData] = await Promise.all([
        competitionsApiService.listTournaments().catch(() => []),
        sportsApiService.listSports().catch(() => []),
        sportsApiService.listTeams().catch(() => []),
        sportsApiService.listPlayers().catch(() => [])
      ]);
      setTournaments(tournamentsData);
      setSports(sportsData);
      setTeams(teamsData);
      setPlayers(playersData);

      if (tournamentsData.length > 0 && !selectedTournamentId) {
        setSelectedTournamentId(tournamentsData[0].id);
      }
    } catch (err) {
      showNotification('error', 'Error al cargar torneos: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const loadTournamentDetails = async (tournamentId) => {
    try {
      const [phasesData, devData] = await Promise.all([
        competitionsApiService.getPhasesByTournament(tournamentId).catch(() => []),
        competitionsApiService.getTournamentDevelopment(tournamentId).catch(() => null)
      ]);
      setPhases(phasesData);
      setTournamentDevelopment(devData);

      if (phasesData.length > 0) {
        setSelectedPhaseId(phasesData[0].id);
      } else {
        setSelectedPhaseId('');
        setGroups([]);
        setMatches([]);
        setStandings([]);
      }
    } catch (err) {
      showNotification('error', 'Error cargando fases del torneo: ' + err.message);
    }
  };

  const loadPhaseDetails = async (phaseId) => {
    try {
      const [groupsData, matchesData, standingsData] = await Promise.all([
        competitionsApiService.getGroupsByPhase(phaseId).catch(() => []),
        competitionsApiService.getMatchesByPhase(phaseId).catch(() => []),
        competitionsApiService.getStandings(phaseId).catch(() => [])
      ]);
      setGroups(groupsData);
      setMatches(matchesData);
      setStandings(standingsData);
    } catch (err) {
      showNotification('error', 'Error cargando detalles de la fase: ' + err.message);
    }
  };

  // --- HU-GC-01: CREAR TORNEO ---
  const handleCreateTournament = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const created = await competitionsApiService.createTournament(newTournament);
      showNotification('success', '¡Torneo oficial creado exitosamente!');
      setNewTournament({
        nombre: '', descripcion: '', deporteId: '',
        fechaInicio: '', fechaFin: '', fechaCierreInscripcion: '', cupoEquipos: 16
      });
      await loadInitialData();
      setSelectedTournamentId(created.id);
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- HU-GC-02: INSCRIBIR EQUIPO ---
  const handleRegisterTeam = async (e) => {
    e.preventDefault();
    if (!selectedTournamentId || !newRegistration.equipoId) return;
    setLoading(true);
    try {
      await competitionsApiService.registerTeam(selectedTournamentId, newRegistration);
      showNotification('success', '¡Equipo inscrito exitosamente en el torneo!');
      setNewRegistration({ equipoId: '', observaciones: '' });
      loadInitialData();
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- HU-GC-03: CREAR FASE ---
  const handleCreatePhase = async (e) => {
    e.preventDefault();
    if (!selectedTournamentId) return;
    setLoading(true);
    try {
      await competitionsApiService.createPhase(selectedTournamentId, {
        nombre: newPhase.nombre,
        tipo: newPhase.tipo,
        orden: parseInt(newPhase.orden, 10),
        fasePadreId: newPhase.fasePadreId || null
      });
      showNotification('success', 'Fase jerárquica creada con éxito.');
      setNewPhase({ nombre: '', tipo: 'GRUPOS', orden: 1, fasePadreId: '' });
      loadTournamentDetails(selectedTournamentId);
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- HU-GC-04: CREAR GRUPO Y ASIGNAR EQUIPO ---
  const handleCreateGroup = async (e) => {
    e.preventDefault();
    if (!selectedPhaseId) return;
    setLoading(true);
    try {
      await competitionsApiService.createGroup(selectedPhaseId, {
        nombre: newGroup.nombre,
        orden: parseInt(newGroup.orden, 10)
      });
      showNotification('success', 'Grupo creado exitosamente.');
      setNewGroup({ nombre: '', orden: 1 });
      loadPhaseDetails(selectedPhaseId);
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAssignTeamToGroup = async (e) => {
    e.preventDefault();
    if (!selectedPhaseId || !assignGroupTeam.groupId || !assignGroupTeam.teamId) return;
    setLoading(true);
    try {
      await competitionsApiService.assignTeamToGroup(selectedPhaseId, assignGroupTeam.groupId, assignGroupTeam.teamId);
      showNotification('success', 'Equipo asignado al grupo sin duplicidad.');
      setAssignGroupTeam({ groupId: '', teamId: '' });
      loadPhaseDetails(selectedPhaseId);
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- HU-GC-05: CREAR LLAVE ---
  const handleCreateBracket = async (e) => {
    e.preventDefault();
    if (!selectedPhaseId) return;
    setLoading(true);
    try {
      await competitionsApiService.createBracket(selectedPhaseId, {
        nombre: newBracket.nombre,
        ronda: parseInt(newBracket.ronda, 10),
        orden: parseInt(newBracket.orden, 10),
        partidoId: null
      });
      showNotification('success', 'Llave eliminatoria estructurada.');
      setNewBracket({ nombre: '', ronda: 1, orden: 1 });
      loadPhaseDetails(selectedPhaseId);
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- HU-GC-06: PROGRAMAR PARTIDO ---
  const handleScheduleMatch = async (e) => {
    e.preventDefault();
    if (!selectedPhaseId) return;
    setLoading(true);
    try {
      await competitionsApiService.scheduleMatch({
        faseId: selectedPhaseId,
        grupoId: selectedGroupId || null,
        equipoLocalId: newMatch.equipoLocalId,
        equipoVisitanteId: newMatch.equipoVisitanteId,
        fechaHoraProgramada: new Date(newMatch.fechaHoraProgramada).toISOString(),
        escenario: newMatch.escenario || 'Estadio Central',
        partidoOrigen1: null,
        partidoOrigen2: null
      });
      showNotification('success', 'Partido programado exitosamente.');
      setNewMatch({ equipoLocalId: '', equipoVisitanteId: '', fechaHoraProgramada: '', escenario: '' });
      loadPhaseDetails(selectedPhaseId);
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- HU-GC-07: RESULTADOS Y EVENTOS ---
  const handleSaveResult = async (matchId, resultData) => {
    setLoading(true);
    try {
      await competitionsApiService.recordMatchResult(matchId, resultData);
      showNotification('success', 'Resultado oficial registrado y tabla de posiciones recalculada automáticamente.');
      setSelectedMatchForActa(null);
      loadPhaseDetails(selectedPhaseId);
      loadTournamentDetails(selectedTournamentId);
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAddEvent = async (matchId, eventData) => {
    setLoading(true);
    try {
      await competitionsApiService.recordMatchEvent(matchId, eventData);
      showNotification('success', 'Evento oficial asentado en el acta del partido.');
      loadPhaseDetails(selectedPhaseId);
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-6xl mx-auto space-y-6 animate-in fade-in duration-300">
      {/* Notifications */}
      {notification && (
        <div className={`p-4 rounded-xl text-xs font-semibold flex items-center gap-2 border ${notification.type === 'success' ? 'bg-emerald-950/60 border-emerald-500/30 text-emerald-300' : 'bg-rose-950/60 border-rose-500/30 text-rose-300'}`}>
          {notification.type === 'success' ? <CheckCircle className="w-4 h-4" /> : <AlertTriangle className="w-4 h-4" />}
          <span>{notification.text}</span>
        </div>
      )}

      {/* Top Banner */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 bg-slate-900/60 border border-slate-800 rounded-2xl p-6 backdrop-blur-md">
        <div>
          <span className="text-[10px] font-extrabold tracking-widest uppercase text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded-full border border-emerald-500/20">
            Módulo 3 • Entrega 2
          </span>
          <h2 className="font-heading text-2xl font-black text-white mt-1">Gestión de Competencias</h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Estructuración de torneos, fases jerárquicas, cruces directos, programación de encuentros y motor FIFA de clasificación.
          </p>
        </div>

        {/* Tournament Selector */}
        <div className="flex items-center gap-2 w-full sm:w-auto">
          <label className="text-xs font-semibold text-slate-300 whitespace-nowrap">Torneo Activo:</label>
          <select
            value={selectedTournamentId}
            onChange={(e) => setSelectedTournamentId(e.target.value)}
            className="px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white text-xs focus:outline-none focus:border-emerald-500"
          >
            {tournaments.map((t) => (
              <option key={t.id} value={t.id}>{t.nombre}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Sub Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-800 pb-2 overflow-x-auto">
        <button
          onClick={() => setActiveTab('tournaments')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${activeTab === 'tournaments' ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/20' : 'text-slate-400 hover:text-white hover:bg-slate-900'}`}
        >
          <Trophy className="w-4 h-4" />
          <span>1. Torneo & Inscripciones (HU-GC-01/02)</span>
        </button>
        <button
          onClick={() => setActiveTab('phases')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${activeTab === 'phases' ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/20' : 'text-slate-400 hover:text-white hover:bg-slate-900'}`}
        >
          <Layers className="w-4 h-4" />
          <span>2. Fases, Grupos & Llaves (HU-GC-03/04/05)</span>
        </button>
        <button
          onClick={() => setActiveTab('matches')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${activeTab === 'matches' ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/20' : 'text-slate-400 hover:text-white hover:bg-slate-900'}`}
        >
          <Calendar className="w-4 h-4" />
          <span>3. Programación & Marcadores (HU-GC-06/07)</span>
        </button>
        <button
          onClick={() => setActiveTab('development')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${activeTab === 'development' ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/20' : 'text-slate-400 hover:text-white hover:bg-slate-900'}`}
        >
          <Award className="w-4 h-4" />
          <span>4. Posiciones & Desarrollo (HU-GC-08)</span>
        </button>
      </div>

      {/* TAB 1: TORNEOS & INSCRIPCIONES */}
      {activeTab === 'tournaments' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Create Tournament Form */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
            <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
              <Plus className="w-4 h-4 text-emerald-400" /> Crear Torneo Oficial (HU-GC-01)
            </h3>
            <form onSubmit={handleCreateTournament} className="space-y-3 text-xs">
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Nombre del Torneo *</label>
                <input
                  type="text"
                  placeholder="Copa Metropolitana SportFlow 2026"
                  value={newTournament.nombre}
                  onChange={(e) => setNewTournament({ ...newTournament, nombre: e.target.value })}
                  required
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                />
              </div>
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Disciplina Deportiva *</label>
                <select
                  value={newTournament.deporteId}
                  onChange={(e) => setNewTournament({ ...newTournament, deporteId: e.target.value })}
                  required
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                >
                  <option value="">-- Seleccionar Deporte --</option>
                  {sports.map((s) => (
                    <option key={s.id} value={s.id}>{s.nombreCanonico}</option>
                  ))}
                </select>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Fecha Inicio *</label>
                  <input
                    type="date"
                    value={newTournament.fechaInicio}
                    onChange={(e) => setNewTournament({ ...newTournament, fechaInicio: e.target.value })}
                    required
                    className="w-full px-2 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Fecha Fin *</label>
                  <input
                    type="date"
                    value={newTournament.fechaFin}
                    onChange={(e) => setNewTournament({ ...newTournament, fechaFin: e.target.value })}
                    required
                    className="w-full px-2 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Cierre Registro *</label>
                  <input
                    type="date"
                    value={newTournament.fechaCierreInscripcion}
                    onChange={(e) => setNewTournament({ ...newTournament, fechaCierreInscripcion: e.target.value })}
                    required
                    className="w-full px-2 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Cupo Equipos</label>
                  <input
                    type="number"
                    min="2"
                    max="128"
                    value={newTournament.cupoEquipos}
                    onChange={(e) => setNewTournament({ ...newTournament, cupoEquipos: parseInt(e.target.value, 10) })}
                    className="w-full px-2 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500 font-mono"
                  />
                </div>
              </div>
              <button
                type="submit"
                disabled={loading || !newTournament.nombre || !newTournament.deporteId}
                className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold transition shadow-lg shadow-emerald-500/20 disabled:opacity-50"
              >
                Crear Torneo
              </button>
            </form>
          </div>

          {/* Enroll Team Form (HU-GC-02) */}
          <div className="space-y-6">
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
              <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
                <Users className="w-4 h-4 text-emerald-400" /> Inscribir Equipo (HU-GC-02)
              </h3>
              <p className="text-xs text-slate-400 mb-3">
                Verifica correspondencia de disciplina, límite de cupos y no duplicidad.
              </p>
              <form onSubmit={handleRegisterTeam} className="space-y-3 text-xs">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Equipo a Inscribir *</label>
                  <select
                    value={newRegistration.equipoId}
                    onChange={(e) => setNewRegistration({ ...newRegistration, equipoId: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="">-- Seleccionar Equipo --</option>
                    {teams.map((t) => (
                      <option key={t.id} value={t.id}>{t.nombreDistintivo} ({t.ciudad})</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Observaciones</label>
                  <input
                    type="text"
                    placeholder="Documentación completa y aval institucional..."
                    value={newRegistration.observaciones}
                    onChange={(e) => setNewRegistration({ ...newRegistration, observaciones: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <button
                  type="submit"
                  disabled={loading || !newRegistration.equipoId}
                  className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold transition shadow-lg shadow-emerald-500/20 disabled:opacity-50"
                >
                  Confirmar Inscripción
                </button>
              </form>
            </div>
          </div>

          {/* List of Tournaments */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
            <h3 className="font-heading text-base font-bold text-white mb-3">Torneos Registrados</h3>
            <div className="space-y-3">
              {tournaments.map((t) => (
                <div
                  key={t.id}
                  onClick={() => setSelectedTournamentId(t.id)}
                  className={`p-4 rounded-xl border cursor-pointer transition text-xs space-y-1 ${t.id === selectedTournamentId ? 'bg-emerald-950/30 border-emerald-500/40 shadow-md' : 'bg-slate-950/60 border-slate-800 hover:border-slate-700'}`}
                >
                  <div className="flex items-center justify-between">
                    <span className="font-bold text-white text-sm">{t.nombre}</span>
                    <span className="px-2 py-0.5 rounded text-[10px] font-extrabold uppercase bg-emerald-500/20 text-emerald-400">
                      {t.estado}
                    </span>
                  </div>
                  <div className="flex items-center justify-between text-slate-400 pt-1">
                    <span>Cupos: <strong className="text-white">{t.equiposInscritos} / {t.cupoEquipos}</strong></span>
                    <span className="font-mono text-[10px]">{t.fechaInicio} al {t.fechaFin}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: FASES, GRUPOS & LLAVES */}
      {activeTab === 'phases' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="space-y-6">
            {/* Create Phase Form (HU-GC-03) */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
              <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
                <Plus className="w-4 h-4 text-emerald-400" /> Crear Fase (HU-GC-03)
              </h3>
              <p className="text-xs text-slate-400 mb-3">Soporta fases jerárquicas y recursivas</p>
              <form onSubmit={handleCreatePhase} className="space-y-3 text-xs">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Nombre de la Fase *</label>
                  <input
                    type="text"
                    placeholder="Ej. Fase de Grupos, Octavos de Final"
                    value={newPhase.nombre}
                    onChange={(e) => setNewPhase({ ...newPhase, nombre: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div className="grid grid-cols-2 gap-2">
                  <div>
                    <label className="block text-slate-300 font-semibold mb-1">Tipo de Fase</label>
                    <select
                      value={newPhase.tipo}
                      onChange={(e) => setNewPhase({ ...newPhase, tipo: e.target.value })}
                      className="w-full px-2 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                    >
                      <option value="GRUPOS">Grupos</option>
                      <option value="ELIMINATORIA_DIRECTA">Eliminatoria Directa</option>
                      <option value="LIGA">Liga</option>
                      <option value="CUADRANGULAR">Cuadrangular</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-slate-300 font-semibold mb-1">Orden</label>
                    <input
                      type="number"
                      min="1"
                      value={newPhase.orden}
                      onChange={(e) => setNewPhase({ ...newPhase, orden: e.target.value })}
                      className="w-full px-2 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500 font-mono"
                    />
                  </div>
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Fase Padre (Subfase Recursiva)</label>
                  <select
                    value={newPhase.fasePadreId}
                    onChange={(e) => setNewPhase({ ...newPhase, fasePadreId: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="">-- Ninguna (Fase Principal) --</option>
                    {phases.map((p) => (
                      <option key={p.id} value={p.id}>{p.nombre} ({p.tipo})</option>
                    ))}
                  </select>
                </div>
                <button
                  type="submit"
                  disabled={loading || !newPhase.nombre}
                  className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold transition shadow-lg shadow-emerald-500/20 disabled:opacity-50"
                >
                  Crear Fase
                </button>
              </form>
            </div>

            {/* Create Group Form (HU-GC-04) */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
              <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
                <Plus className="w-4 h-4 text-emerald-400" /> Crear Grupo (HU-GC-04)
              </h3>
              <form onSubmit={handleCreateGroup} className="space-y-3 text-xs">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Nombre del Grupo</label>
                  <input
                    type="text"
                    placeholder="Ej. Grupo A, Grupo B"
                    value={newGroup.nombre}
                    onChange={(e) => setNewGroup({ ...newGroup, nombre: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <button
                  type="submit"
                  disabled={loading || !selectedPhaseId || !newGroup.nombre}
                  className="w-full py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 font-bold transition disabled:opacity-50"
                >
                  Guardar Grupo
                </button>
              </form>
            </div>
          </div>

          {/* Group Team Assignment and Brackets */}
          <div className="space-y-6">
            {/* Assign Team to Group Form */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
              <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
                <Users className="w-4 h-4 text-emerald-400" /> Asignar Equipo a Grupo
              </h3>
              <p className="text-xs text-slate-400 mb-3">Evita duplicidad en la misma fase.</p>
              <form onSubmit={handleAssignTeamToGroup} className="space-y-3 text-xs">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Grupo Destino *</label>
                  <select
                    value={assignGroupTeam.groupId}
                    onChange={(e) => setAssignGroupTeam({ ...assignGroupTeam, groupId: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="">-- Seleccionar Grupo --</option>
                    {groups.map((g) => (
                      <option key={g.id} value={g.id}>{g.nombre}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Equipo Inscrito *</label>
                  <select
                    value={assignGroupTeam.teamId}
                    onChange={(e) => setAssignGroupTeam({ ...assignGroupTeam, teamId: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="">-- Seleccionar Equipo --</option>
                    {teams.map((t) => (
                      <option key={t.id} value={t.id}>{t.nombreDistintivo}</option>
                    ))}
                  </select>
                </div>
                <button
                  type="submit"
                  disabled={loading || !assignGroupTeam.groupId || !assignGroupTeam.teamId}
                  className="w-full py-2.5 rounded-xl bg-emerald-500/20 hover:bg-emerald-500/30 text-emerald-300 border border-emerald-500/30 font-bold transition disabled:opacity-50"
                >
                  Asignar a Grupo
                </button>
              </form>
            </div>

            {/* Create Playoff Bracket (HU-GC-05) */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
              <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
                <Shield className="w-4 h-4 text-emerald-400" /> Crear Llave Eliminatoria (HU-GC-05)
              </h3>
              <form onSubmit={handleCreateBracket} className="space-y-3 text-xs">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Nombre del Cruce *</label>
                  <input
                    type="text"
                    placeholder="Ej. Cuartos 1: 1A vs 2B"
                    value={newBracket.nombre}
                    onChange={(e) => setNewBracket({ ...newBracket, nombre: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <button
                  type="submit"
                  disabled={loading || !selectedPhaseId || !newBracket.nombre}
                  className="w-full py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 font-bold transition disabled:opacity-50"
                >
                  Configurar Llave
                </button>
              </form>
            </div>
          </div>

          {/* Phase and Groups Visualizer */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md space-y-4">
            <h3 className="font-heading text-base font-bold text-white">Estructura de Fases del Torneo</h3>
            {phases.length === 0 ? (
              <p className="text-xs text-slate-500">Este torneo aún no tiene fases configuradas.</p>
            ) : (
              <div className="space-y-3">
                {phases.map((p) => (
                  <div
                    key={p.id}
                    onClick={() => setSelectedPhaseId(p.id)}
                    className={`p-3 rounded-xl border cursor-pointer transition text-xs ${p.id === selectedPhaseId ? 'bg-emerald-950/30 border-emerald-500/40 shadow-sm' : 'bg-slate-950/60 border-slate-800 hover:border-slate-700'}`}
                  >
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-white text-sm">{p.nombre}</span>
                      <span className="px-2 py-0.5 rounded text-[10px] font-extrabold uppercase bg-slate-800 text-slate-300">
                        {p.tipo}
                      </span>
                    </div>
                    {p.fasePadreId && (
                      <span className="text-[10px] text-emerald-400 font-mono block mt-1">Subfase vinculada</span>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 3: PARTIDOS & RESULTADOS */}
      {activeTab === 'matches' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Schedule Match Form (HU-GC-06) */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
            <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
              <Plus className="w-4 h-4 text-emerald-400" /> Programar Partido (HU-GC-06)
            </h3>
            <p className="text-xs text-slate-400 mb-3">En fase seleccionada</p>
            <form onSubmit={handleScheduleMatch} className="space-y-3 text-xs">
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Grupo (Opcional)</label>
                <select
                  value={selectedGroupId}
                  onChange={(e) => setSelectedGroupId(e.target.value)}
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                >
                  <option value="">-- Sin Grupo (Eliminatoria / Libre) --</option>
                  {groups.map((g) => (
                    <option key={g.id} value={g.id}>{g.nombre}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-slate-300 font-semibold mb-1">Equipo Local *</label>
                <select
                  value={newMatch.equipoLocalId}
                  onChange={(e) => setNewMatch({ ...newMatch, equipoLocalId: e.target.value })}
                  required
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                >
                  <option value="">-- Seleccionar Local --</option>
                  {teams.map((t) => (
                    <option key={t.id} value={t.id}>{t.nombreDistintivo}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-slate-300 font-semibold mb-1">Equipo Visitante *</label>
                <select
                  value={newMatch.equipoVisitanteId}
                  onChange={(e) => setNewMatch({ ...newMatch, equipoVisitanteId: e.target.value })}
                  required
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                >
                  <option value="">-- Seleccionar Visitante --</option>
                  {teams.map((t) => (
                    <option key={t.id} value={t.id}>{t.nombreDistintivo}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-slate-300 font-semibold mb-1">Fecha y Hora Programada *</label>
                <input
                  type="datetime-local"
                  value={newMatch.fechaHoraProgramada}
                  onChange={(e) => setNewMatch({ ...newMatch, fechaHoraProgramada: e.target.value })}
                  required
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                />
              </div>

              <div>
                <label className="block text-slate-300 font-semibold mb-1">Escenario Deportivo</label>
                <input
                  type="text"
                  placeholder="Estadio Alberto Grisales"
                  value={newMatch.escenario}
                  onChange={(e) => setNewMatch({ ...newMatch, escenario: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                />
              </div>

              <button
                type="submit"
                disabled={loading || !newMatch.equipoLocalId || !newMatch.equipoVisitanteId || !newMatch.fechaHoraProgramada}
                className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold transition shadow-lg shadow-emerald-500/20 disabled:opacity-50"
              >
                Programar Partido
              </button>
            </form>
          </div>

          {/* List of Matches in Phase */}
          <div className="lg:col-span-2 bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
            <h3 className="font-heading text-base font-bold text-white mb-3">Encuentros de la Fase ({matches.length})</h3>
            {matches.length === 0 ? (
              <p className="text-xs text-slate-500">No hay partidos programados en esta fase.</p>
            ) : (
              <div className="space-y-3">
                {matches.map((m) => (
                  <div key={m.id} className="p-4 bg-slate-950/60 border border-slate-800/80 rounded-xl space-y-2 text-xs">
                    <div className="flex items-center justify-between">
                      <span className="text-slate-400">{m.escenario || 'Cancha Principal'}</span>
                      <span className={`px-2 py-0.5 rounded text-[10px] font-extrabold uppercase ${m.estado === 'FINALIZADO' ? 'bg-emerald-500/20 text-emerald-400' : 'bg-slate-800 text-slate-300'}`}>
                        {m.estado}
                      </span>
                    </div>

                    <div className="grid grid-cols-5 items-center gap-2 py-1">
                      <div className="col-span-2 font-bold text-white text-sm text-right">
                        {m.nombreEquipoLocal}
                      </div>
                      <div className="col-span-1 text-center font-black text-base text-emerald-400 font-mono">
                        {m.resultadoConfirmado && m.resultado
                          ? `${m.resultado.golesLocal} - ${m.resultado.golesVisitante}`
                          : 'VS'}
                      </div>
                      <div className="col-span-2 font-bold text-white text-sm text-left">
                        {m.nombreEquipoVisitante}
                      </div>
                    </div>

                    <div className="pt-2 border-t border-slate-900 flex items-center justify-between text-slate-500 text-[11px]">
                      <span>{new Date(m.fechaHoraProgramada).toLocaleString()}</span>
                      <button
                        onClick={() => setSelectedMatchForActa(m)}
                        className="px-2.5 py-1 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 text-xs font-semibold flex items-center gap-1 transition"
                      >
                        <Flag className="w-3.5 h-3.5" />
                        <span>{m.resultadoConfirmado ? 'Ver Acta Oficial' : 'Registrar Resultado'}</span>
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 4: POSICIONES & DESARROLLO */}
      {activeTab === 'development' && (
        <div className="space-y-6">
          {/* Real-time recalculated Standings */}
          <StandingsTable
            standings={standings}
            title={`Tabla de Posiciones - ${phases.find(p => p.id === selectedPhaseId)?.nombre || 'Fase Actual'}`}
          />

          {/* Tournament Development Tree View (HU-GC-08) */}
          {tournamentDevelopment && (
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-6 backdrop-blur-md space-y-4">
              <h3 className="font-heading text-lg font-bold text-white flex items-center gap-2">
                <Trophy className="w-5 h-5 text-amber-400" />
                <span>Vista Integral del Desarrollo: {tournamentDevelopment.nombre}</span>
              </h3>
              <p className="text-xs text-slate-400">
                Resumen gerencial de fases, grupos y cruces eliminatorios (HU-GC-08)
              </p>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {tournamentDevelopment.fases?.map((f) => (
                  <div key={f.faseId} className="bg-slate-950/60 border border-slate-800 rounded-xl p-4 space-y-2 text-xs">
                    <div className="flex items-center justify-between border-b border-slate-900 pb-2">
                      <strong className="text-white text-sm">{f.nombre}</strong>
                      <span className="text-[10px] uppercase font-bold text-emerald-400 bg-emerald-500/10 px-2 py-0.5 rounded">
                        {f.tipo}
                      </span>
                    </div>

                    <div className="text-slate-400">
                      Grupos: <strong className="text-white">{f.grupos?.length || 0}</strong> • Partidos: <strong className="text-white">{f.partidos?.length || 0}</strong> • Llaves: <strong className="text-white">{f.llaves?.length || 0}</strong>
                    </div>

                    {f.grupos && f.grupos.length > 0 && (
                      <div className="pt-2">
                        {f.grupos.map((g) => (
                          <div key={g.grupoId} className="mb-2">
                            <span className="text-emerald-300 font-bold block mb-1">{g.nombre}</span>
                            <div className="space-y-1">
                              {g.tablaPosiciones?.slice(0, 3).map((pos) => (
                                <div key={pos.equipoId} className="flex justify-between text-[11px] text-slate-300 bg-slate-900/40 px-2 py-1 rounded">
                                  <span>{pos.posicion}. {pos.nombreEquipo}</span>
                                  <span className="font-bold text-emerald-400">{pos.puntos} pts (DG: {pos.diferenciaGoles})</span>
                                </div>
                              ))}
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Match Result / Event Modal */}
      {selectedMatchForActa && (
        <MatchResultModal
          match={selectedMatchForActa}
          players={players}
          onSaveResult={handleSaveResult}
          onAddEvent={handleAddEvent}
          onClose={() => setSelectedMatchForActa(null)}
          loading={loading}
        />
      )}
    </div>
  );
}
