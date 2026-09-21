import React, { useState, useEffect } from 'react';
import {
  Trophy, Shield, Users, Award, Plus, Eye, ArrowRightLeft, GitBranch, RefreshCw, CheckCircle, AlertTriangle
} from 'lucide-react';
import { sportsApiService } from '../infrastructure/sportsApiService';
import PlayerProfileModal from './PlayerProfileModal';
import NewTransferModal from './NewTransferModal';

export default function SportsManager() {
  const [activeTab, setActiveTab] = useState('sports'); // sports | teams | players | skills
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState(null); // { type, text }

  // Data states
  const [sports, setSports] = useState([]);
  const [teams, setTeams] = useState([]);
  const [players, setPlayers] = useState([]);
  const [skills, setSkills] = useState([]);

  // Selected for modals
  const [selectedHierarchy, setSelectedHierarchy] = useState(null);
  const [selectedPlayerProfile, setSelectedPlayerProfile] = useState(null);
  const [transferPlayer, setTransferPlayer] = useState(null);

  // Forms states
  const [newSport, setNewSport] = useState({ nombreCanonico: '', descripcion: '', deportePadreId: '' });
  const [newTeam, setNewTeam] = useState({ nombreDistintivo: '', ciudad: '', categoria: 'Primera A', genero: 'MASCULINO' });
  const [selectedTeamForSport, setSelectedTeamForSport] = useState({ teamId: '', sportId: '' });
  const [newPlayer, setNewPlayer] = useState({ tipoDocumento: 'CC', numeroIdentificacion: '', nombres: '', apellidos: '', fechaNacimiento: '', posicion: 'Delantero' });
  const [newSkill, setNewSkill] = useState({ nombreCanonico: '', descripcion: '' });
  const [assignSkill, setAssignSkill] = useState({ playerId: '', habilidadId: '', nivel: 'INTERMEDIO', observacion: '' });

  useEffect(() => {
    loadAllData();
  }, []);

  const showNotification = (type, text) => {
    setNotification({ type, text });
    setTimeout(() => setNotification(null), 4000);
  };

  const loadAllData = async () => {
    setLoading(true);
    try {
      const [sportsData, teamsData, playersData, skillsData] = await Promise.all([
        sportsApiService.listSports().catch(() => []),
        sportsApiService.listTeams().catch(() => []),
        sportsApiService.listPlayers().catch(() => []),
        sportsApiService.listSkills().catch(() => [])
      ]);
      setSports(sportsData);
      setTeams(teamsData);
      setPlayers(playersData);
      setSkills(skillsData);
    } catch (err) {
      showNotification('error', 'Error cargando datos deportivos: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- Handlers HU-GD-01 ---
  const handleCreateSport = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await sportsApiService.createSport({
        nombreCanonico: newSport.nombreCanonico,
        descripcion: newSport.descripcion,
        deportePadreId: newSport.deportePadreId || null
      });
      showNotification('success', '¡Disciplina registrada exitosamente!');
      setNewSport({ nombreCanonico: '', descripcion: '', deportePadreId: '' });
      loadAllData();
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleViewHierarchy = async (sportId) => {
    try {
      const data = await sportsApiService.getSportHierarchy(sportId);
      setSelectedHierarchy(data);
    } catch (err) {
      showNotification('error', err.message);
    }
  };

  // --- Handlers HU-GD-02 & HU-GD-03 ---
  const handleCreateTeam = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await sportsApiService.createTeam({
        ...newTeam,
        clubId: null
      });
      showNotification('success', '¡Equipo deportivo registrado exitosamente!');
      setNewTeam({ nombreDistintivo: '', ciudad: '', categoria: 'Primera A', genero: 'MASCULINO' });
      loadAllData();
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAssociateSport = async (e) => {
    e.preventDefault();
    if (!selectedTeamForSport.teamId || !selectedTeamForSport.sportId) return;
    setLoading(true);
    try {
      await sportsApiService.associateSportToTeam(selectedTeamForSport.teamId, selectedTeamForSport.sportId);
      showNotification('success', 'Disciplina asociada al equipo exitosamente.');
      loadAllData();
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- Handlers HU-GD-04 & HU-GD-05 ---
  const handleCreatePlayer = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await sportsApiService.createPlayer({
        ...newPlayer,
        personaId: null
      });
      showNotification('success', '¡Jugador registrado en la base de datos oficial!');
      setNewPlayer({ tipoDocumento: 'CC', numeroIdentificacion: '', nombres: '', apellidos: '', fechaNacimiento: '', posicion: 'Delantero' });
      loadAllData();
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenTransferModal = (player) => {
    setTransferPlayer(player);
  };

  const handleSubmitTransfer = async (playerId, contractData) => {
    setLoading(true);
    try {
      await sportsApiService.createContract(playerId, contractData);
      showNotification('success', 'Fichaje confirmado y registrado en el historial inmutable.');
      setTransferPlayer(null);
      loadAllData();
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleViewPlayerProfile = async (playerId) => {
    try {
      const profile = await sportsApiService.getPlayerSportsProfile(playerId);
      setSelectedPlayerProfile(profile);
    } catch (err) {
      showNotification('error', err.message);
    }
  };

  // --- Handlers HU-GD-06 & HU-GD-07 ---
  const handleCreateSkill = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await sportsApiService.createSkill(newSkill);
      showNotification('success', 'Habilidad registrada en el catálogo oficial.');
      setNewSkill({ nombreCanonico: '', descripcion: '' });
      loadAllData();
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAssignSkill = async (e) => {
    e.preventDefault();
    if (!assignSkill.playerId || !assignSkill.habilidadId) return;
    setLoading(true);
    try {
      await sportsApiService.assignSkillToPlayer(assignSkill.playerId, {
        habilidadId: assignSkill.habilidadId,
        nivel: assignSkill.nivel,
        observacion: assignSkill.observacion
      });
      showNotification('success', 'Habilidad evaluada y asignada al jugador.');
      setAssignSkill({ playerId: '', habilidadId: '', nivel: 'INTERMEDIO', observacion: '' });
    } catch (err) {
      showNotification('error', err.message);
    } finally {
      setLoading(false);
    }
  };

  // Recursive renderer for sport hierarchy
  const renderHierarchyNode = (node) => {
    return (
      <div key={node.id} className="border-l-2 border-emerald-500/40 pl-3 ml-2 mt-2 text-xs">
        <div className="flex items-center gap-2 py-1">
          <span className="w-2 h-2 rounded-full bg-emerald-400"></span>
          <strong className="text-white text-sm">{node.nombreCanonico}</strong>
          {node.descripcion && <span className="text-slate-400 text-xs">({node.descripcion})</span>}
        </div>
        {node.subdeportes && node.subdeportes.length > 0 && (
          <div className="space-y-1">
            {node.subdeportes.map(renderHierarchyNode)}
          </div>
        )}
      </div>
    );
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
            Módulo 2 • Entrega 2
          </span>
          <h2 className="font-heading text-2xl font-black text-white mt-1">Gestión Deportiva</h2>
          <p className="text-xs text-slate-400 mt-0.5">
            Administración integral de disciplinas recursivas, escuadras, fichajes históricos y catálogo de habilidades.
          </p>
        </div>
        <button
          onClick={loadAllData}
          disabled={loading}
          className="flex items-center gap-2 px-3 py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-bold transition"
        >
          <RefreshCw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
          <span>Actualizar Datos</span>
        </button>
      </div>

      {/* Sub Tabs */}
      <div className="flex items-center gap-2 border-b border-slate-800 pb-2 overflow-x-auto">
        <button
          onClick={() => setActiveTab('sports')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${activeTab === 'sports' ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/20' : 'text-slate-400 hover:text-white hover:bg-slate-900'}`}
        >
          <Trophy className="w-4 h-4" />
          <span>1. Disciplinas & Jerarquías ({sports.length})</span>
        </button>
        <button
          onClick={() => setActiveTab('teams')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${activeTab === 'teams' ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/20' : 'text-slate-400 hover:text-white hover:bg-slate-900'}`}
        >
          <Shield className="w-4 h-4" />
          <span>2. Equipos & Deportes M:N ({teams.length})</span>
        </button>
        <button
          onClick={() => setActiveTab('players')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${activeTab === 'players' ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/20' : 'text-slate-400 hover:text-white hover:bg-slate-900'}`}
        >
          <Users className="w-4 h-4" />
          <span>3. Jugadores & Fichajes ({players.length})</span>
        </button>
        <button
          onClick={() => setActiveTab('skills')}
          className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-bold transition ${activeTab === 'skills' ? 'bg-emerald-500 text-slate-950 shadow-md shadow-emerald-500/20' : 'text-slate-400 hover:text-white hover:bg-slate-900'}`}
        >
          <Award className="w-4 h-4" />
          <span>4. Habilidades Deportivas ({skills.length})</span>
        </button>
      </div>

      {/* TAB 1: DISCIPLINAS */}
      {activeTab === 'sports' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Create Sport Form */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
            <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
              <Plus className="w-4 h-4 text-emerald-400" /> Registrar Disciplina / Subdeporte
            </h3>
            <p className="text-xs text-slate-400 mb-4">Soporta jerarquías recursivas (HU-GD-01)</p>
            <form onSubmit={handleCreateSport} className="space-y-3 text-xs">
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Nombre Canónico *</label>
                <input
                  type="text"
                  placeholder="Ej. Fútbol, Baloncesto, Natación"
                  value={newSport.nombreCanonico}
                  onChange={(e) => setNewSport({ ...newSport, nombreCanonico: e.target.value })}
                  required
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                />
              </div>
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Descripción</label>
                <textarea
                  rows="2"
                  placeholder="Reglamento general o descripción de la disciplina..."
                  value={newSport.descripcion}
                  onChange={(e) => setNewSport({ ...newSport, descripcion: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                />
              </div>
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Deporte Padre (Para Subdeporte Recursivo)</label>
                <select
                  value={newSport.deportePadreId}
                  onChange={(e) => setNewSport({ ...newSport, deportePadreId: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                >
                  <option value="">-- Ninguno (Disciplina Raíz) --</option>
                  {sports.map((s) => (
                    <option key={s.id} value={s.id}>{s.nombreCanonico}</option>
                  ))}
                </select>
              </div>
              <button
                type="submit"
                disabled={loading || !newSport.nombreCanonico}
                className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold transition shadow-lg shadow-emerald-500/20 disabled:opacity-50"
              >
                Guardar Disciplina
              </button>
            </form>
          </div>

          {/* List and Hierarchy Preview */}
          <div className="lg:col-span-2 space-y-4">
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
              <h3 className="font-heading text-base font-bold text-white mb-3">Catálogo de Disciplinas Registradas</h3>
              {sports.length === 0 ? (
                <p className="text-xs text-slate-500">No hay disciplinas registradas aún.</p>
              ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {sports.map((s) => (
                    <div key={s.id} className="p-3 bg-slate-950/60 border border-slate-800/80 rounded-xl flex items-center justify-between">
                      <div>
                        <span className="font-bold text-white text-sm block">{s.nombreCanonico}</span>
                        <span className="text-xs text-slate-400">{s.descripcion || 'Sin descripción'}</span>
                        {s.deportePadreId && (
                          <span className="text-[10px] text-emerald-400 font-mono block mt-1">Subdeporte recursivo</span>
                        )}
                      </div>
                      <button
                        onClick={() => handleViewHierarchy(s.id)}
                        className="px-2.5 py-1.5 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 text-xs font-semibold flex items-center gap-1 transition"
                      >
                        <GitBranch className="w-3.5 h-3.5" /> Árbol
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Hierarchy Tree Modal / Card */}
            {selectedHierarchy && (
              <div className="bg-slate-950/90 border border-emerald-500/30 rounded-2xl p-5">
                <div className="flex items-center justify-between border-b border-slate-800 pb-2 mb-3">
                  <h4 className="text-sm font-bold text-white flex items-center gap-2">
                    <GitBranch className="w-4 h-4 text-emerald-400" />
                    Jerarquía Genealógica de: <span className="text-emerald-400">{selectedHierarchy.nombreCanonico}</span>
                  </h4>
                  <button
                    onClick={() => setSelectedHierarchy(null)}
                    className="text-slate-400 hover:text-white text-xs"
                  >
                    Cerrar
                  </button>
                </div>
                {renderHierarchyNode(selectedHierarchy)}
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 2: EQUIPOS */}
      {activeTab === 'teams' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="space-y-6">
            {/* Create Team Form */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
              <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
                <Plus className="w-4 h-4 text-emerald-400" /> Registrar Equipo (HU-GD-02)
              </h3>
              <form onSubmit={handleCreateTeam} className="space-y-3 text-xs">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Nombre Distintivo *</label>
                  <input
                    type="text"
                    placeholder="Ej. Águilas Doradas FC"
                    value={newTeam.nombreDistintivo}
                    onChange={(e) => setNewTeam({ ...newTeam, nombreDistintivo: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Ciudad *</label>
                  <input
                    type="text"
                    placeholder="Ej. Medellín, Bogotá, Cali"
                    value={newTeam.ciudad}
                    onChange={(e) => setNewTeam({ ...newTeam, ciudad: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div className="grid grid-cols-2 gap-2">
                  <div>
                    <label className="block text-slate-300 font-semibold mb-1">Categoría</label>
                    <input
                      type="text"
                      value={newTeam.categoria}
                      onChange={(e) => setNewTeam({ ...newTeam, categoria: e.target.value })}
                      className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                    />
                  </div>
                  <div>
                    <label className="block text-slate-300 font-semibold mb-1">Género</label>
                    <select
                      value={newTeam.genero}
                      onChange={(e) => setNewTeam({ ...newTeam, genero: e.target.value })}
                      className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                    >
                      <option value="MASCULINO">Masculino</option>
                      <option value="FEMENINO">Femenino</option>
                      <option value="MIXTO">Mixto</option>
                    </select>
                  </div>
                </div>
                <button
                  type="submit"
                  disabled={loading || !newTeam.nombreDistintivo}
                  className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold transition shadow-lg shadow-emerald-500/20 disabled:opacity-50"
                >
                  Registrar Equipo
                </button>
              </form>
            </div>

            {/* Associate Sport Form (HU-GD-03) */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
              <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
                <Trophy className="w-4 h-4 text-emerald-400" /> Asociación M:N (HU-GD-03)
              </h3>
              <p className="text-xs text-slate-400 mb-3">Vincula una disciplina deportiva a un equipo existente.</p>
              <form onSubmit={handleAssociateSport} className="space-y-3 text-xs">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Equipo *</label>
                  <select
                    value={selectedTeamForSport.teamId}
                    onChange={(e) => setSelectedTeamForSport({ ...selectedTeamForSport, teamId: e.target.value })}
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
                  <label className="block text-slate-300 font-semibold mb-1">Disciplina a Asociar *</label>
                  <select
                    value={selectedTeamForSport.sportId}
                    onChange={(e) => setSelectedTeamForSport({ ...selectedTeamForSport, sportId: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="">-- Seleccionar Deporte --</option>
                    {sports.map((s) => (
                      <option key={s.id} value={s.id}>{s.nombreCanonico}</option>
                    ))}
                  </select>
                </div>
                <button
                  type="submit"
                  disabled={loading || !selectedTeamForSport.teamId || !selectedTeamForSport.sportId}
                  className="w-full py-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 font-bold transition disabled:opacity-50"
                >
                  Asociar Disciplina
                </button>
              </form>
            </div>
          </div>

          {/* List of Teams */}
          <div className="lg:col-span-2 bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
            <h3 className="font-heading text-base font-bold text-white mb-3">Equipos Oficiales Registrados</h3>
            {teams.length === 0 ? (
              <p className="text-xs text-slate-500">No hay equipos registrados aún.</p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {teams.map((t) => (
                  <div key={t.id} className="p-4 bg-slate-950/60 border border-slate-800/80 rounded-xl space-y-2 text-xs">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-white text-sm">{t.nombreDistintivo}</span>
                      <span className="px-2 py-0.5 rounded text-[10px] font-extrabold uppercase bg-emerald-500/20 text-emerald-400">
                        {t.genero}
                      </span>
                    </div>
                    <p className="text-slate-400">
                      Sede: <strong className="text-slate-200">{t.ciudad}</strong> • Cat: <strong className="text-slate-200">{t.categoria}</strong>
                    </p>
                    <div className="pt-2 border-t border-slate-900 flex items-center justify-between text-slate-500 text-[11px]">
                      <span>Deportes asignados: {t.deporteIds ? t.deporteIds.length : 0}</span>
                      <span className="font-mono">{t.fechaInscripcion}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 3: JUGADORES */}
      {activeTab === 'players' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Register Player Form */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
            <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
              <Plus className="w-4 h-4 text-emerald-400" /> Registrar Jugador (HU-GD-04)
            </h3>
            <p className="text-xs text-slate-400 mb-4">Validación de documento único</p>
            <form onSubmit={handleCreatePlayer} className="space-y-3 text-xs">
              <div className="grid grid-cols-3 gap-2">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Tipo Doc *</label>
                  <select
                    value={newPlayer.tipoDocumento}
                    onChange={(e) => setNewPlayer({ ...newPlayer, tipoDocumento: e.target.value })}
                    className="w-full px-2 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="CC">CC</option>
                    <option value="TI">TI</option>
                    <option value="PASAPORTE">Pasaporte</option>
                  </select>
                </div>
                <div className="col-span-2">
                  <label className="block text-slate-300 font-semibold mb-1">Nº Identificación *</label>
                  <input
                    type="text"
                    placeholder="1098765432"
                    value={newPlayer.numeroIdentificacion}
                    onChange={(e) => setNewPlayer({ ...newPlayer, numeroIdentificacion: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500 font-mono"
                  />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Nombres *</label>
                  <input
                    type="text"
                    placeholder="Mateo"
                    value={newPlayer.nombres}
                    onChange={(e) => setNewPlayer({ ...newPlayer, nombres: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Apellidos *</label>
                  <input
                    type="text"
                    placeholder="Gómez"
                    value={newPlayer.apellidos}
                    onChange={(e) => setNewPlayer({ ...newPlayer, apellidos: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
              </div>
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Fecha Nacimiento *</label>
                <input
                  type="date"
                  value={newPlayer.fechaNacimiento}
                  onChange={(e) => setNewPlayer({ ...newPlayer, fechaNacimiento: e.target.value })}
                  required
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                />
              </div>
              <div>
                <label className="block text-slate-300 font-semibold mb-1">Posición Deportiva</label>
                <input
                  type="text"
                  placeholder="Delantero, Medio, Portero..."
                  value={newPlayer.posicion}
                  onChange={(e) => setNewPlayer({ ...newPlayer, posicion: e.target.value })}
                  className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                />
              </div>
              <button
                type="submit"
                disabled={loading || !newPlayer.nombres || !newPlayer.numeroIdentificacion}
                className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold transition shadow-lg shadow-emerald-500/20 disabled:opacity-50"
              >
                Registrar Jugador
              </button>
            </form>
          </div>

          {/* List of Players */}
          <div className="lg:col-span-2 bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
            <h3 className="font-heading text-base font-bold text-white mb-3">Plantel de Jugadores Registrados</h3>
            {players.length === 0 ? (
              <p className="text-xs text-slate-500">No hay jugadores registrados aún.</p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {players.map((p) => (
                  <div key={p.id} className="p-4 bg-slate-950/60 border border-slate-800/80 rounded-xl space-y-3 text-xs">
                    <div className="flex items-center justify-between">
                      <span className="font-bold text-white text-sm">{p.nombreCompleto}</span>
                      <span className="px-2 py-0.5 rounded text-[10px] font-extrabold uppercase bg-emerald-500/20 text-emerald-400">
                        {p.estado}
                      </span>
                    </div>
                    <p className="text-slate-400">
                      {p.tipoDocumento}: <span className="font-mono text-slate-200">{p.numeroIdentificacion}</span> • Pos: <strong className="text-emerald-300">{p.posicion || 'Polifuncional'}</strong>
                    </p>
                    <div className="pt-2 border-t border-slate-900 flex items-center justify-between gap-2">
                      <button
                        onClick={() => handleViewPlayerProfile(p.id)}
                        className="px-2.5 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold flex items-center gap-1 transition"
                      >
                        <Eye className="w-3.5 h-3.5 text-emerald-400" /> Perfil Integral
                      </button>
                      <button
                        onClick={() => handleOpenTransferModal(p)}
                        className="px-2.5 py-1.5 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 text-xs font-semibold flex items-center gap-1 transition"
                      >
                        <ArrowRightLeft className="w-3.5 h-3.5" /> Fichar / Traspaso
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 4: HABILIDADES */}
      {activeTab === 'skills' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="space-y-6">
            {/* Create Skill */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
              <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
                <Plus className="w-4 h-4 text-emerald-400" /> Catálogo de Habilidad (HU-GD-06)
              </h3>
              <form onSubmit={handleCreateSkill} className="space-y-3 text-xs">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Nombre de la Habilidad *</label>
                  <input
                    type="text"
                    placeholder="Ej. Regate en velocidad, Definición, Salto"
                    value={newSkill.nombreCanonico}
                    onChange={(e) => setNewSkill({ ...newSkill, nombreCanonico: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Descripción</label>
                  <textarea
                    rows="2"
                    placeholder="Descripción técnica y criterios de evaluación..."
                    value={newSkill.descripcion}
                    onChange={(e) => setNewSkill({ ...newSkill, descripcion: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <button
                  type="submit"
                  disabled={loading || !newSkill.nombreCanonico}
                  className="w-full py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold transition shadow-lg shadow-emerald-500/20 disabled:opacity-50"
                >
                  Registrar Habilidad
                </button>
              </form>
            </div>

            {/* Assign Skill to Player (HU-GD-07) */}
            <div className="bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
              <h3 className="font-heading text-base font-bold text-white mb-2 flex items-center gap-2">
                <Award className="w-4 h-4 text-emerald-400" /> Evaluar Jugador (HU-GD-07)
              </h3>
              <form onSubmit={handleAssignSkill} className="space-y-3 text-xs">
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Jugador *</label>
                  <select
                    value={assignSkill.playerId}
                    onChange={(e) => setAssignSkill({ ...assignSkill, playerId: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="">-- Seleccionar Jugador --</option>
                    {players.map((p) => (
                      <option key={p.id} value={p.id}>{p.nombreCompleto} ({p.numeroIdentificacion})</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Habilidad *</label>
                  <select
                    value={assignSkill.habilidadId}
                    onChange={(e) => setAssignSkill({ ...assignSkill, habilidadId: e.target.value })}
                    required
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="">-- Seleccionar Habilidad --</option>
                    {skills.map((s) => (
                      <option key={s.id} value={s.id}>{s.nombreCanonico}</option>
                    ))}
                  </select>
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Nivel Alcanzado</label>
                  <select
                    value={assignSkill.nivel}
                    onChange={(e) => setAssignSkill({ ...assignSkill, nivel: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  >
                    <option value="PRINCIPIANTE">Principiante</option>
                    <option value="INTERMEDIO">Intermedio</option>
                    <option value="AVANZADO">Avanzado</option>
                    <option value="EXPERTO">Experto / Élite</option>
                  </select>
                </div>
                <div>
                  <label className="block text-slate-300 font-semibold mb-1">Observación Técnica</label>
                  <input
                    type="text"
                    placeholder="Excelente precisión de remate..."
                    value={assignSkill.observacion}
                    onChange={(e) => setAssignSkill({ ...assignSkill, observacion: e.target.value })}
                    className="w-full px-3 py-2 rounded-xl bg-slate-950 border border-slate-800 text-white focus:outline-none focus:border-emerald-500"
                  />
                </div>
                <button
                  type="submit"
                  disabled={loading || !assignSkill.playerId || !assignSkill.habilidadId}
                  className="w-full py-2 rounded-xl bg-emerald-500/20 hover:bg-emerald-500/30 text-emerald-300 border border-emerald-500/30 font-bold transition disabled:opacity-50"
                >
                  Asignar y Evaluar
                </button>
              </form>
            </div>
          </div>

          {/* List of Skills */}
          <div className="lg:col-span-2 bg-slate-900/60 border border-slate-800 rounded-2xl p-5 backdrop-blur-md">
            <h3 className="font-heading text-base font-bold text-white mb-3">Catálogo Oficial de Habilidades</h3>
            {skills.length === 0 ? (
              <p className="text-xs text-slate-500">No hay habilidades registradas en el catálogo.</p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {skills.map((sk) => (
                  <div key={sk.id} className="p-4 bg-slate-950/60 border border-slate-800/80 rounded-xl space-y-1 text-xs">
                    <span className="font-bold text-white text-sm block">{sk.nombreCanonico}</span>
                    <p className="text-slate-400">{sk.descripcion || 'Sin descripción técnica'}</p>
                    <span className="text-[10px] text-slate-500 font-mono block pt-1">
                      Creado: {sk.creadoEn ? new Date(sk.creadoEn).toLocaleDateString() : 'Activo'}
                    </span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* Modals */}
      {selectedPlayerProfile && (
        <PlayerProfileModal
          profile={selectedPlayerProfile}
          onClose={() => setSelectedPlayerProfile(null)}
        />
      )}

      {transferPlayer && (
        <NewTransferModal
          player={transferPlayer}
          teams={teams}
          onSubmit={handleSubmitTransfer}
          onClose={() => setTransferPlayer(null)}
          loading={loading}
        />
      )}
    </div>
  );
}
