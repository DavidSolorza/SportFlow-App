package com.sportflow.features.sports.infrastructure.controllers;

import com.sportflow.features.sports.application.dto.*;
import com.sportflow.features.sports.application.usecases.ManagePlayerContractUseCase;
import com.sportflow.features.sports.application.usecases.ManageTeamUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {

    private final ManageTeamUseCase manageTeamUseCase;
    private final ManagePlayerContractUseCase managePlayerContractUseCase;

    public TeamController(ManageTeamUseCase manageTeamUseCase,
                          ManagePlayerContractUseCase managePlayerContractUseCase) {
        this.manageTeamUseCase = manageTeamUseCase;
        this.managePlayerContractUseCase = managePlayerContractUseCase;
    }

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        TeamResponse response = manageTeamUseCase.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> listTeams() {
        return ResponseEntity.ok(manageTeamUseCase.listTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable UUID id) {
        return ResponseEntity.ok(manageTeamUseCase.getTeamById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTeamRequest request) {
        return ResponseEntity.ok(manageTeamUseCase.updateTeam(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTeam(@PathVariable UUID id) {
        manageTeamUseCase.deleteOrDeactivateTeam(id);
        return ResponseEntity.ok(Map.of("message", "Equipo eliminado exitosamente del sistema."));
    }

    @PostMapping("/{teamId}/sports/{sportId}")
    public ResponseEntity<Map<String, String>> associateSport(
            @PathVariable UUID teamId,
            @PathVariable UUID sportId) {
        manageTeamUseCase.associateSportToTeam(teamId, sportId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Deporte asociado exitosamente al equipo."));
    }

    @DeleteMapping("/{teamId}/sports/{sportId}")
    public ResponseEntity<Map<String, String>> disassociateSport(
            @PathVariable UUID teamId,
            @PathVariable UUID sportId) {
        manageTeamUseCase.disassociateSportFromTeam(teamId, sportId);
        return ResponseEntity.ok(Map.of("message", "Asociación entre equipo y deporte retirada con éxito."));
    }

    @GetMapping("/{teamId}/sports")
    public ResponseEntity<List<SportResponse>> getSportsByTeam(@PathVariable UUID teamId) {
        return ResponseEntity.ok(manageTeamUseCase.getSportsByTeam(teamId));
    }

    @GetMapping("/{teamId}/roster")
    public ResponseEntity<List<ContractResponse>> getTeamRoster(@PathVariable UUID teamId) {
        return ResponseEntity.ok(managePlayerContractUseCase.getTeamRoster(teamId));
    }
}
