package com.sportflow.features.sports.infrastructure.controllers;

import com.sportflow.features.sports.application.dto.CreateSkillRequest;
import com.sportflow.features.sports.application.dto.SkillResponse;
import com.sportflow.features.sports.application.usecases.ManageSkillUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final ManageSkillUseCase manageSkillUseCase;

    public SkillController(ManageSkillUseCase manageSkillUseCase) {
        this.manageSkillUseCase = manageSkillUseCase;
    }

    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(@Valid @RequestBody CreateSkillRequest request) {
        SkillResponse response = manageSkillUseCase.createSkill(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SkillResponse>> listSkills() {
        return ResponseEntity.ok(manageSkillUseCase.listSkills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getSkillById(@PathVariable UUID id) {
        return ResponseEntity.ok(manageSkillUseCase.getSkillById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable UUID id,
            @Valid @RequestBody CreateSkillRequest request) {
        return ResponseEntity.ok(manageSkillUseCase.updateSkill(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSkill(@PathVariable UUID id) {
        manageSkillUseCase.deleteSkill(id);
        return ResponseEntity.ok(Map.of("message", "Habilidad deportiva eliminada del catálogo."));
    }
}
