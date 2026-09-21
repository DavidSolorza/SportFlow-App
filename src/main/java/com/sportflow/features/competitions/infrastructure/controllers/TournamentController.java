package com.sportflow.features.competitions.infrastructure.controllers;

import com.sportflow.features.competitions.application.dto.*;
import com.sportflow.features.competitions.application.usecases.ManageTournamentUseCase;
import com.sportflow.features.competitions.application.usecases.TournamentDevelopmentUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

    private final ManageTournamentUseCase manageTournamentUseCase;
    private final TournamentDevelopmentUseCase tournamentDevelopmentUseCase;

    public TournamentController(ManageTournamentUseCase manageTournamentUseCase,
                                TournamentDevelopmentUseCase tournamentDevelopmentUseCase) {
        this.manageTournamentUseCase = manageTournamentUseCase;
        this.tournamentDevelopmentUseCase = tournamentDevelopmentUseCase;
    }

    // --- HU-GC-01: CREAR TORNEO ---
    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(@Valid @RequestBody CreateTournamentRequest request) {
        TournamentResponse response = manageTournamentUseCase.createTournament(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> listTournaments(
            @RequestParam(name = "sportId", required = false) UUID sportId) {
        return ResponseEntity.ok(manageTournamentUseCase.listTournaments(sportId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournamentById(@PathVariable UUID id) {
        return ResponseEntity.ok(manageTournamentUseCase.getTournamentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponse> updateTournament(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTournamentRequest request) {
        return ResponseEntity.ok(manageTournamentUseCase.updateTournament(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTournament(@PathVariable UUID id) {
        manageTournamentUseCase.deleteOrCancelTournament(id);
        return ResponseEntity.ok(Map.of("message", "Torneo procesado o eliminado exitosamente."));
    }

    // --- HU-GC-02: INSCRIBIR EQUIPO ---
    @PostMapping("/{id}/register")
    public ResponseEntity<RegistrationResponse> registerTeam(
            @PathVariable UUID id,
            @Valid @RequestBody RegisterTeamRequest request) {
        RegistrationResponse response = manageTournamentUseCase.registerTeam(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/registrations")
    public ResponseEntity<List<RegistrationResponse>> getTournamentRegistrations(@PathVariable UUID id) {
        return ResponseEntity.ok(manageTournamentUseCase.getTournamentRegistrations(id));
    }

    // --- HU-GC-08: VISTA INTEGRAL DEL DESARROLLO DEL TORNEO ---
    @GetMapping("/{id}/development")
    public ResponseEntity<TournamentDevelopmentResponse> getTournamentDevelopment(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentDevelopmentUseCase.getTournamentDevelopment(id));
    }
}
