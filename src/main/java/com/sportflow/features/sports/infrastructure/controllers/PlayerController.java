package com.sportflow.features.sports.infrastructure.controllers;

import com.sportflow.features.sports.application.dto.*;
import com.sportflow.features.sports.application.usecases.GetPlayerSportsProfileUseCase;
import com.sportflow.features.sports.application.usecases.ManagePlayerContractUseCase;
import com.sportflow.features.sports.application.usecases.ManagePlayerUseCase;
import com.sportflow.features.sports.application.usecases.ManageSkillUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final ManagePlayerUseCase managePlayerUseCase;
    private final ManagePlayerContractUseCase managePlayerContractUseCase;
    private final ManageSkillUseCase manageSkillUseCase;
    private final GetPlayerSportsProfileUseCase getPlayerSportsProfileUseCase;

    public PlayerController(ManagePlayerUseCase managePlayerUseCase,
                            ManagePlayerContractUseCase managePlayerContractUseCase,
                            ManageSkillUseCase manageSkillUseCase,
                            GetPlayerSportsProfileUseCase getPlayerSportsProfileUseCase) {
        this.managePlayerUseCase = managePlayerUseCase;
        this.managePlayerContractUseCase = managePlayerContractUseCase;
        this.manageSkillUseCase = manageSkillUseCase;
        this.getPlayerSportsProfileUseCase = getPlayerSportsProfileUseCase;
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        PlayerResponse response = managePlayerUseCase.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> listPlayers() {
        return ResponseEntity.ok(managePlayerUseCase.listPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable UUID id) {
        return ResponseEntity.ok(managePlayerUseCase.getPlayerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponse> updatePlayer(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlayerRequest request) {
        return ResponseEntity.ok(managePlayerUseCase.updatePlayer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePlayer(@PathVariable UUID id) {
        managePlayerUseCase.deleteOrDeactivatePlayer(id);
        return ResponseEntity.ok(Map.of("message", "Jugador eliminado o desactivado correctamente."));
    }

    // --- HU-GD-05: CONTRATOS Y TRASPASOS ---

    @PostMapping("/{id}/contracts")
    public ResponseEntity<ContractResponse> assignPlayerToTeam(
            @PathVariable UUID id,
            @Valid @RequestBody CreateContractRequest request) {
        ContractResponse response = managePlayerContractUseCase.assignPlayerToTeam(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/contracts/{contractId}/terminate")
    public ResponseEntity<ContractResponse> terminateContract(
            @PathVariable UUID id,
            @PathVariable UUID contractId,
            @Valid @RequestBody TerminateContractRequest request) {
        return ResponseEntity.ok(managePlayerContractUseCase.terminateContract(id, contractId, request));
    }

    @GetMapping("/{id}/transfers")
    public ResponseEntity<List<ContractResponse>> getPlayerTransferHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(managePlayerContractUseCase.getPlayerTransferHistory(id));
    }

    // --- HU-GD-07: ASIGNACIÓN DE HABILIDADES ---

    @PostMapping("/{id}/skills")
    public ResponseEntity<PlayerSkillResponse> assignSkill(
            @PathVariable UUID id,
            @Valid @RequestBody AssignSkillRequest request) {
        PlayerSkillResponse response = manageSkillUseCase.assignSkillToPlayer(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/skills/{skillId}")
    public ResponseEntity<Map<String, String>> removeSkill(
            @PathVariable UUID id,
            @PathVariable UUID skillId) {
        manageSkillUseCase.removeSkillFromPlayer(id, skillId);
        return ResponseEntity.ok(Map.of("message", "Habilidad deportiva retirada del jugador."));
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<List<PlayerSkillResponse>> getPlayerSkills(@PathVariable UUID id) {
        return ResponseEntity.ok(manageSkillUseCase.getPlayerSkills(id));
    }

    // --- HU-GD-08: PERFIL DEPORTIVO INTEGRAL ---

    @GetMapping("/{id}/profile")
    public ResponseEntity<PlayerProfileResponse> getPlayerSportsProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(getPlayerSportsProfileUseCase.getPlayerSportsProfile(id));
    }
}
