package com.sportflow.features.competitions.application.usecases;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.ConflictException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.competitions.application.dto.CreateTournamentRequest;
import com.sportflow.features.competitions.application.dto.RegisterTeamRequest;
import com.sportflow.features.competitions.application.dto.RegistrationResponse;
import com.sportflow.features.competitions.application.dto.TournamentResponse;
import com.sportflow.features.competitions.application.dto.UpdateTournamentRequest;
import com.sportflow.features.competitions.domain.model.Tournament;
import com.sportflow.features.competitions.domain.model.TournamentRegistration;
import com.sportflow.features.competitions.domain.ports.PhaseRepositoryPort;
import com.sportflow.features.competitions.domain.ports.TournamentRegistrationRepositoryPort;
import com.sportflow.features.competitions.domain.ports.TournamentRepositoryPort;
import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.ports.SportRepositoryPort;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ManageTournamentUseCase {

    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final TournamentRegistrationRepositoryPort registrationRepositoryPort;
    private final SportRepositoryPort sportRepositoryPort;
    private final TeamRepositoryPort teamRepositoryPort;
    private final PhaseRepositoryPort phaseRepositoryPort;

    public ManageTournamentUseCase(TournamentRepositoryPort tournamentRepositoryPort,
                                  TournamentRegistrationRepositoryPort registrationRepositoryPort,
                                  SportRepositoryPort sportRepositoryPort,
                                  TeamRepositoryPort teamRepositoryPort,
                                  PhaseRepositoryPort phaseRepositoryPort) {
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.registrationRepositoryPort = registrationRepositoryPort;
        this.sportRepositoryPort = sportRepositoryPort;
        this.teamRepositoryPort = teamRepositoryPort;
        this.phaseRepositoryPort = phaseRepositoryPort;
    }

    public TournamentResponse createTournament(CreateTournamentRequest request) {
        sportRepositoryPort.findById(request.deporteId())
                .orElseThrow(() -> new EntityNotFoundException("El deporte especificado no existe."));

        Tournament tournament = Tournament.crear(
                request.deporteId(),
                request.nombre(),
                request.descripcion(),
                request.fechaInicio(),
                request.fechaFin(),
                request.fechaCierreInscripcion(),
                request.cupoEquipos()
        );
        Tournament saved = tournamentRepositoryPort.save(tournament);
        return mapToResponse(saved, 0);
    }

    public TournamentResponse updateTournament(UUID id, UpdateTournamentRequest request) {
        Tournament tournament = tournamentRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torneo no encontrado con ID: " + id));

        tournament.actualizar(
                request.nombre(),
                request.descripcion(),
                request.fechaInicio(),
                request.fechaFin(),
                request.fechaCierreInscripcion(),
                request.cupoEquipos(),
                request.estado()
        );
        Tournament saved = tournamentRepositoryPort.save(tournament);
        int inscritos = registrationRepositoryPort.countAcceptedByTournamentId(id);
        return mapToResponse(saved, inscritos);
    }

    public void deleteOrCancelTournament(UUID id) {
        Tournament tournament = tournamentRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torneo no encontrado con ID: " + id));

        if (phaseRepositoryPort.existsByTournamentId(id)) {
            throw new BusinessRuleException("No se puede eliminar el torneo porque ya posee fases y partidos programados. " +
                    "Proceda a cancelarlo si la competencia se suspende.");
        }

        tournamentRepositoryPort.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TournamentResponse getTournamentById(UUID id) {
        Tournament tournament = tournamentRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Torneo no encontrado con ID: " + id));
        int inscritos = registrationRepositoryPort.countAcceptedByTournamentId(id);
        return mapToResponse(tournament, inscritos);
    }

    @Transactional(readOnly = true)
    public List<TournamentResponse> listTournaments(UUID sportId) {
        List<Tournament> tournaments = sportId != null
                ? tournamentRepositoryPort.findBySportId(sportId)
                : tournamentRepositoryPort.findAll();

        return tournaments.stream().map(t -> {
            int inscritos = registrationRepositoryPort.countAcceptedByTournamentId(t.getId());
            return mapToResponse(t, inscritos);
        }).toList();
    }

    // --- HU-GC-02: GESTIONAR INSCRIPCIONES ---

    public RegistrationResponse registerTeam(UUID tournamentId, RegisterTeamRequest request) {
        Tournament tournament = tournamentRepositoryPort.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Torneo no encontrado con ID: " + tournamentId));

        if (!tournament.estaEnPeriodoDeInscripcion()) {
            throw new BusinessRuleException("El torneo no se encuentra en período de registro abierto o ya venció la fecha de cierre.");
        }

        Team team = teamRepositoryPort.findById(request.equipoId())
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + request.equipoId()));

        if (!team.practicaDeporte(tournament.getSportId())) {
            throw new BusinessRuleException("El equipo " + team.getNombreDistintivo() +
                    " no tiene asociado el deporte requerido para este torneo.");
        }

        if (registrationRepositoryPort.existsByTournamentIdAndTeamId(tournamentId, request.equipoId())) {
            throw new ConflictException("El equipo ya se encuentra inscrito en este torneo.");
        }

        int actualesInscritos = registrationRepositoryPort.countAcceptedByTournamentId(tournamentId);
        if (actualesInscritos >= tournament.getCupoEquipos()) {
            throw new BusinessRuleException("El cupo máximo de equipos (" + tournament.getCupoEquipos() +
                    ") para este torneo ha sido completado.");
        }

        TournamentRegistration registration = TournamentRegistration.crear(
                tournamentId,
                request.equipoId(),
                request.observaciones()
        );

        TournamentRegistration saved = registrationRepositoryPort.save(registration);
        return new RegistrationResponse(
                saved.getId(),
                saved.getTournamentId(),
                saved.getTeamId(),
                team.getNombreDistintivo(),
                saved.getFechaInscripcion(),
                saved.getEstado(),
                saved.getObservaciones()
        );
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> getTournamentRegistrations(UUID tournamentId) {
        tournamentRepositoryPort.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Torneo no encontrado con ID: " + tournamentId));

        return registrationRepositoryPort.findByTournamentId(tournamentId).stream().map(r -> {
            Team t = teamRepositoryPort.findById(r.getTeamId()).orElse(null);
            return new RegistrationResponse(
                    r.getId(),
                    r.getTournamentId(),
                    r.getTeamId(),
                    t != null ? t.getNombreDistintivo() : "Equipo",
                    r.getFechaInscripcion(),
                    r.getEstado(),
                    r.getObservaciones()
            );
        }).toList();
    }

    private TournamentResponse mapToResponse(Tournament t, int inscritos) {
        return new TournamentResponse(
                t.getId(),
                t.getSportId(),
                t.getNombre(),
                t.getDescripcion(),
                t.getFechaInicio(),
                t.getFechaFin(),
                t.getFechaCierreInscripcion(),
                t.getCupoEquipos(),
                inscritos,
                t.getEstado()
        );
    }
}
