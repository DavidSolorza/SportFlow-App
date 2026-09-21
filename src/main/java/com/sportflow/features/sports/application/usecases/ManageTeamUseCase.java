package com.sportflow.features.sports.application.usecases;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.ConflictException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.sports.application.dto.CreateTeamRequest;
import com.sportflow.features.sports.application.dto.SportResponse;
import com.sportflow.features.sports.application.dto.TeamResponse;
import com.sportflow.features.sports.application.dto.UpdateTeamRequest;
import com.sportflow.features.sports.domain.model.Sport;
import com.sportflow.features.sports.domain.model.Team;
import com.sportflow.features.sports.domain.ports.ClubRepositoryPort;
import com.sportflow.features.sports.domain.ports.PlayerContractRepositoryPort;
import com.sportflow.features.sports.domain.ports.SportRepositoryPort;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ManageTeamUseCase {

    private final TeamRepositoryPort teamRepositoryPort;
    private final SportRepositoryPort sportRepositoryPort;
    private final ClubRepositoryPort clubRepositoryPort;
    private final PlayerContractRepositoryPort playerContractRepositoryPort;

    public ManageTeamUseCase(TeamRepositoryPort teamRepositoryPort,
                             SportRepositoryPort sportRepositoryPort,
                             ClubRepositoryPort clubRepositoryPort,
                             PlayerContractRepositoryPort playerContractRepositoryPort) {
        this.teamRepositoryPort = teamRepositoryPort;
        this.sportRepositoryPort = sportRepositoryPort;
        this.clubRepositoryPort = clubRepositoryPort;
        this.playerContractRepositoryPort = playerContractRepositoryPort;
    }

    public TeamResponse createTeam(CreateTeamRequest request) {
        if (request.clubId() != null) {
            clubRepositoryPort.findById(request.clubId())
                    .orElseThrow(() -> new EntityNotFoundException("El club especificado no existe."));
        }

        Team team = Team.crear(
                request.clubId(),
                request.nombreDistintivo(),
                request.ciudad(),
                request.categoria(),
                request.genero()
        );
        Team saved = teamRepositoryPort.save(team);
        return mapToResponse(saved);
    }

    public TeamResponse updateTeam(UUID id, UpdateTeamRequest request) {
        Team team = teamRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + id));

        if (request.clubId() != null) {
            clubRepositoryPort.findById(request.clubId())
                    .orElseThrow(() -> new EntityNotFoundException("El club especificado no existe."));
        }

        team.actualizar(
                request.nombreDistintivo(),
                request.ciudad(),
                request.categoria(),
                request.genero(),
                request.estado(),
                request.clubId()
        );
        Team saved = teamRepositoryPort.save(team);
        return mapToResponse(saved);
    }

    public void deleteOrDeactivateTeam(UUID id) {
        Team team = teamRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + id));

        if (!playerContractRepositoryPort.findActiveContractsByTeamId(id).isEmpty()) {
            throw new BusinessRuleException("No se puede eliminar el equipo porque cuenta con jugadores con contrato activo.");
        }

        teamRepositoryPort.deleteById(id);
    }

    public void associateSportToTeam(UUID teamId, UUID sportId) {
        Team team = teamRepositoryPort.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + teamId));

        sportRepositoryPort.findById(sportId)
                .orElseThrow(() -> new EntityNotFoundException("Deporte no encontrado con ID: " + sportId));

        if (team.practicaDeporte(sportId)) {
            throw new ConflictException("El equipo ya se encuentra asociado al deporte especificado.");
        }

        team.asociarDeporte(sportId);
        teamRepositoryPort.save(team);
    }

    public void disassociateSportFromTeam(UUID teamId, UUID sportId) {
        Team team = teamRepositoryPort.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + teamId));

        sportRepositoryPort.findById(sportId)
                .orElseThrow(() -> new EntityNotFoundException("Deporte no encontrado con ID: " + sportId));

        team.desasociarDeporte(sportId);
        teamRepositoryPort.save(team);
    }

    @Transactional(readOnly = true)
    public TeamResponse getTeamById(UUID id) {
        Team team = teamRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + id));
        return mapToResponse(team);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> listTeams() {
        return teamRepositoryPort.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SportResponse> getSportsByTeam(UUID teamId) {
        Team team = teamRepositoryPort.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Equipo no encontrado con ID: " + teamId));

        return team.getDeporteIds().stream()
                .map(sportRepositoryPort::findById)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(this::mapSportToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getTeamsBySport(UUID sportId) {
        sportRepositoryPort.findById(sportId)
                .orElseThrow(() -> new EntityNotFoundException("Deporte no encontrado con ID: " + sportId));

        return teamRepositoryPort.findByDeporteId(sportId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private TeamResponse mapToResponse(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getClubId(),
                team.getNombreDistintivo(),
                team.getCiudad(),
                team.getCategoria(),
                team.getGenero(),
                team.getEstado(),
                team.getFechaInscripcion(),
                team.getDeporteIds()
        );
    }

    private SportResponse mapSportToResponse(Sport sport) {
        return new SportResponse(
                sport.getId(),
                sport.getNombreCanonico(),
                sport.getDescripcion(),
                sport.isActivo(),
                sport.getDeportePadreId(),
                sport.getCreadoEn(),
                sport.getActualizadoEn()
        );
    }
}
