package com.sportflow.features.competitions.infrastructure.controllers;

import com.sportflow.features.competitions.application.dto.*;
import com.sportflow.features.competitions.application.usecases.ManageMatchUseCase;
import com.sportflow.features.competitions.application.usecases.ManagePhaseUseCase;
import com.sportflow.features.competitions.application.usecases.TournamentDevelopmentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/phases")
public class PhaseController {

    private final ManagePhaseUseCase managePhaseUseCase;
    private final ManageMatchUseCase manageMatchUseCase;
    private final TournamentDevelopmentUseCase tournamentDevelopmentUseCase;

    public PhaseController(ManagePhaseUseCase managePhaseUseCase,
                           ManageMatchUseCase manageMatchUseCase,
                           TournamentDevelopmentUseCase tournamentDevelopmentUseCase) {
        this.managePhaseUseCase = managePhaseUseCase;
        this.manageMatchUseCase = manageMatchUseCase;
        this.tournamentDevelopmentUseCase = tournamentDevelopmentUseCase;
    }

    // --- HU-GC-03: GESTIONAR FASES ---
    @PostMapping("/tournament/{tournamentId}")
    public ResponseEntity<PhaseResponse> createPhase(
            @PathVariable UUID tournamentId,
            @Valid @RequestBody CreatePhaseRequest request) {
        PhaseResponse response = managePhaseUseCase.createPhase(tournamentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<PhaseResponse>> getPhasesByTournament(@PathVariable UUID tournamentId) {
        return ResponseEntity.ok(managePhaseUseCase.getPhasesByTournament(tournamentId));
    }

    // --- HU-GC-04: GESTIONAR GRUPOS ---
    @PostMapping("/{phaseId}/groups")
    public ResponseEntity<GroupResponse> createGroup(
            @PathVariable UUID phaseId,
            @Valid @RequestBody CreateGroupRequest request) {
        GroupResponse response = managePhaseUseCase.createGroup(phaseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{phaseId}/groups")
    public ResponseEntity<List<GroupResponse>> getGroupsByPhase(@PathVariable UUID phaseId) {
        return ResponseEntity.ok(managePhaseUseCase.getGroupsByPhase(phaseId));
    }

    @PostMapping("/{phaseId}/groups/{groupId}/teams/{teamId}")
    public ResponseEntity<Map<String, String>> assignTeamToGroup(
            @PathVariable UUID phaseId,
            @PathVariable UUID groupId,
            @PathVariable UUID teamId) {
        managePhaseUseCase.assignTeamToGroup(phaseId, groupId, teamId);
        return ResponseEntity.ok(Map.of("message", "Equipo asignado exitosamente al grupo de la fase."));
    }

    // --- HU-GC-05: GESTIONAR LLAVES (PLAYOFFS) ---
    @PostMapping("/{phaseId}/brackets")
    public ResponseEntity<BracketResponse> createBracket(
            @PathVariable UUID phaseId,
            @Valid @RequestBody CreateBracketRequest request) {
        BracketResponse response = managePhaseUseCase.createBracket(phaseId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{phaseId}/brackets")
    public ResponseEntity<List<BracketResponse>> getBracketsByPhase(@PathVariable UUID phaseId) {
        return ResponseEntity.ok(managePhaseUseCase.getBracketsByPhase(phaseId));
    }

    // --- CONSULTA DE PARTIDOS Y CLASIFICACIÓN ---
    @GetMapping("/{phaseId}/matches")
    public ResponseEntity<List<MatchResponse>> getMatchesByPhase(@PathVariable UUID phaseId) {
        return ResponseEntity.ok(manageMatchUseCase.getMatchesByPhase(phaseId));
    }

    @GetMapping("/{phaseId}/standings")
    public ResponseEntity<List<StandingsResponse>> getStandingsByPhase(
            @PathVariable UUID phaseId,
            @RequestParam(name = "groupId", required = false) UUID groupId) {
        return ResponseEntity.ok(tournamentDevelopmentUseCase.getStandingsByPhaseAndGroup(phaseId, groupId));
    }

    @PostMapping("/{phaseId}/groups/{groupId}/calculate-standings")
    public ResponseEntity<List<StandingsResponse>> calculateGroupStandings(
            @PathVariable UUID phaseId,
            @PathVariable UUID groupId) {
        return ResponseEntity.ok(tournamentDevelopmentUseCase.calculateGroupStandings(phaseId, groupId));
    }
}
