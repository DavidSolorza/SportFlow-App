package com.sportflow.features.competitions.application.usecases;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.ConflictException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.competitions.application.dto.*;
import com.sportflow.features.competitions.domain.model.CompetitionGroup;
import com.sportflow.features.competitions.domain.model.Phase;
import com.sportflow.features.competitions.domain.model.Tournament;
import com.sportflow.features.competitions.domain.model.TournamentBracket;
import com.sportflow.features.competitions.domain.ports.*;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ManagePhaseUseCase {

    private final PhaseRepositoryPort phaseRepositoryPort;
    private final TournamentRepositoryPort tournamentRepositoryPort;
    private final CompetitionGroupRepositoryPort groupRepositoryPort;
    private final TournamentRegistrationRepositoryPort registrationRepositoryPort;
    private final TournamentBracketRepositoryPort bracketRepositoryPort;
    private final TeamRepositoryPort teamRepositoryPort;

    public ManagePhaseUseCase(PhaseRepositoryPort phaseRepositoryPort,
                              TournamentRepositoryPort tournamentRepositoryPort,
                              CompetitionGroupRepositoryPort groupRepositoryPort,
                              TournamentRegistrationRepositoryPort registrationRepositoryPort,
                              TournamentBracketRepositoryPort bracketRepositoryPort,
                              TeamRepositoryPort teamRepositoryPort) {
        this.phaseRepositoryPort = phaseRepositoryPort;
        this.tournamentRepositoryPort = tournamentRepositoryPort;
        this.groupRepositoryPort = groupRepositoryPort;
        this.registrationRepositoryPort = registrationRepositoryPort;
        this.bracketRepositoryPort = bracketRepositoryPort;
        this.teamRepositoryPort = teamRepositoryPort;
    }

    // --- HU-GC-03: GESTIONAR FASES ---

    public PhaseResponse createPhase(UUID tournamentId, CreatePhaseRequest request) {
        tournamentRepositoryPort.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("Torneo no encontrado con ID: " + tournamentId));

        if (request.fasePadreId() != null) {
            phaseRepositoryPort.findById(request.fasePadreId())
                    .orElseThrow(() -> new EntityNotFoundException("La fase padre especificada no existe."));
        }

        Phase phase = Phase.crear(
                tournamentId,
                request.nombre(),
                request.tipo(),
                request.orden(),
                request.fasePadreId()
        );
        Phase saved = phaseRepositoryPort.save(phase);
        return mapPhaseToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PhaseResponse> getPhasesByTournament(UUID tournamentId) {
        return phaseRepositoryPort.findByTournamentIdOrderByOrdenAsc(tournamentId)
                .stream()
                .map(this::mapPhaseToResponse)
                .toList();
    }

    // --- HU-GC-04: GESTIONAR GRUPOS ---

    public GroupResponse createGroup(UUID phaseId, CreateGroupRequest request) {
        phaseRepositoryPort.findById(phaseId)
                .orElseThrow(() -> new EntityNotFoundException("Fase no encontrada con ID: " + phaseId));

        CompetitionGroup group = CompetitionGroup.crear(phaseId, request.nombre(), request.orden());
        CompetitionGroup saved = groupRepositoryPort.save(group);
        return mapGroupToResponse(saved);
    }

    public void assignTeamToGroup(UUID phaseId, UUID groupId, UUID teamId) {
        Phase phase = phaseRepositoryPort.findById(phaseId)
                .orElseThrow(() -> new EntityNotFoundException("Fase no encontrada con ID: " + phaseId));

        CompetitionGroup group = groupRepositoryPort.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Grupo no encontrado con ID: " + groupId));

        if (!group.getPhaseId().equals(phaseId)) {
            throw new BusinessRuleException("El grupo especificado no pertenece a esta fase.");
        }

        teamRepositoryPort.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + teamId));

        // Validar que el equipo esté debidamente inscrito en el torneo
        if (!registrationRepositoryPort.existsByTournamentIdAndTeamId(phase.getTournamentId(), teamId)) {
            throw new BusinessRuleException("El equipo no está inscrito en el torneo al que pertenece esta fase.");
        }

        // Validar que el equipo no esté ya asignado a ningún grupo dentro de la misma fase
        if (groupRepositoryPort.existsByPhaseIdAndEquipoId(phaseId, teamId)) {
            throw new ConflictException("El equipo ya se encuentra asignado a un grupo en esta fase.");
        }

        group.agregarEquipo(teamId);
        groupRepositoryPort.save(group);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getGroupsByPhase(UUID phaseId) {
        return groupRepositoryPort.findByPhaseIdOrderByOrdenAsc(phaseId)
                .stream()
                .map(this::mapGroupToResponse)
                .toList();
    }

    // --- HU-GC-05: GESTIONAR LLAVES (PLAYOFFS) ---

    public BracketResponse createBracket(UUID phaseId, CreateBracketRequest request) {
        phaseRepositoryPort.findById(phaseId)
                .orElseThrow(() -> new EntityNotFoundException("Fase no encontrada con ID: " + phaseId));

        TournamentBracket bracket = TournamentBracket.crear(
                phaseId,
                request.nombre(),
                request.ronda(),
                request.orden(),
                request.partidoId()
        );
        TournamentBracket saved = bracketRepositoryPort.save(bracket);
        return mapBracketToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BracketResponse> getBracketsByPhase(UUID phaseId) {
        return bracketRepositoryPort.findByPhaseIdOrderByRondaAscOrdenAsc(phaseId)
                .stream()
                .map(this::mapBracketToResponse)
                .toList();
    }

    private PhaseResponse mapPhaseToResponse(Phase p) {
        return new PhaseResponse(
                p.getId(),
                p.getTournamentId(),
                p.getNombre(),
                p.getTipo(),
                p.getOrden(),
                p.getEstado(),
                p.getFasePadreId()
        );
    }

    private GroupResponse mapGroupToResponse(CompetitionGroup g) {
        return new GroupResponse(
                g.getId(),
                g.getPhaseId(),
                g.getNombre(),
                g.getOrden(),
                g.getEquipoIds()
        );
    }

    private BracketResponse mapBracketToResponse(TournamentBracket b) {
        return new BracketResponse(
                b.getId(),
                b.getPhaseId(),
                b.getNombre(),
                b.getRonda(),
                b.getOrden(),
                b.getPartidoId(),
                b.getGanadorEquipoId()
        );
    }
}
