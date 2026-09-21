package com.sportflow.features.sports.infrastructure.controllers;

import com.sportflow.features.sports.application.dto.CreateSportRequest;
import com.sportflow.features.sports.application.dto.SportHierarchyResponse;
import com.sportflow.features.sports.application.dto.SportResponse;
import com.sportflow.features.sports.application.dto.TeamResponse;
import com.sportflow.features.sports.application.dto.UpdateSportRequest;
import com.sportflow.features.sports.application.usecases.ManageSportUseCase;
import com.sportflow.features.sports.application.usecases.ManageTeamUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sports")
public class SportController {

    private final ManageSportUseCase manageSportUseCase;
    private final ManageTeamUseCase manageTeamUseCase;

    public SportController(ManageSportUseCase manageSportUseCase, ManageTeamUseCase manageTeamUseCase) {
        this.manageSportUseCase = manageSportUseCase;
        this.manageTeamUseCase = manageTeamUseCase;
    }

    @PostMapping
    public ResponseEntity<SportResponse> createSport(@Valid @RequestBody CreateSportRequest request) {
        SportResponse response = manageSportUseCase.createSport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SportResponse>> listSports(
            @RequestParam(name = "soloActivos", required = false) Boolean soloActivos) {
        return ResponseEntity.ok(manageSportUseCase.listSports(soloActivos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SportResponse> getSportById(@PathVariable UUID id) {
        return ResponseEntity.ok(manageSportUseCase.getSportById(id));
    }

    @GetMapping("/{id}/hierarchy")
    public ResponseEntity<SportHierarchyResponse> getSportHierarchy(@PathVariable UUID id) {
        return ResponseEntity.ok(manageSportUseCase.getSportHierarchy(id));
    }

    @GetMapping("/{id}/teams")
    public ResponseEntity<List<TeamResponse>> getTeamsBySport(@PathVariable UUID id) {
        return ResponseEntity.ok(manageTeamUseCase.getTeamsBySport(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SportResponse> updateSport(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSportRequest request) {
        return ResponseEntity.ok(manageSportUseCase.updateSport(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSport(@PathVariable UUID id) {
        manageSportUseCase.deleteOrDeactivateSport(id);
        return ResponseEntity.ok(Map.of("message", "Deporte eliminado exitosamente de la plataforma."));
    }
}
