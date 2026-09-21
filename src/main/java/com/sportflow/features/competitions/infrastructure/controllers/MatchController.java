package com.sportflow.features.competitions.infrastructure.controllers;

import com.sportflow.features.competitions.application.dto.*;
import com.sportflow.features.competitions.application.usecases.ManageMatchUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/matches")
public class MatchController {

    private final ManageMatchUseCase manageMatchUseCase;

    public MatchController(ManageMatchUseCase manageMatchUseCase) {
        this.manageMatchUseCase = manageMatchUseCase;
    }

    // --- HU-GC-06: PROGRAMAR PARTIDO ---
    @PostMapping
    public ResponseEntity<MatchResponse> scheduleMatch(@Valid @RequestBody ScheduleMatchRequest request) {
        MatchResponse response = manageMatchUseCase.scheduleMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable UUID id) {
        return ResponseEntity.ok(manageMatchUseCase.getMatchById(id));
    }

    // --- HU-GC-07: REGISTRAR RESULTADO OFICIAL ---
    @PostMapping("/{id}/result")
    public ResponseEntity<MatchResultResponse> recordMatchResult(
            @PathVariable UUID id,
            @Valid @RequestBody RecordMatchResultRequest request) {
        MatchResultResponse response = manageMatchUseCase.recordMatchResult(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // --- HU-GC-07: REGISTRAR EVENTO / GOL / TARJETA ---
    @PostMapping("/{id}/events")
    public ResponseEntity<MatchEventResponse> recordMatchEvent(
            @PathVariable UUID id,
            @Valid @RequestBody RecordMatchEventRequest request) {
        MatchEventResponse response = manageMatchUseCase.recordMatchEvent(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<List<MatchEventResponse>> getMatchEvents(@PathVariable UUID id) {
        return ResponseEntity.ok(manageMatchUseCase.getMatchEvents(id));
    }
}
