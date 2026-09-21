package com.sportflow.features.sports.application.usecases;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.ConflictException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.sports.application.dto.AssignSkillRequest;
import com.sportflow.features.sports.application.dto.CreateSkillRequest;
import com.sportflow.features.sports.application.dto.PlayerSkillResponse;
import com.sportflow.features.sports.application.dto.SkillResponse;
import com.sportflow.features.sports.domain.model.Player;
import com.sportflow.features.sports.domain.model.PlayerSkill;
import com.sportflow.features.sports.domain.model.Skill;
import com.sportflow.features.sports.domain.ports.PlayerRepositoryPort;
import com.sportflow.features.sports.domain.ports.PlayerSkillRepositoryPort;
import com.sportflow.features.sports.domain.ports.SkillRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ManageSkillUseCase {

    private final SkillRepositoryPort skillRepositoryPort;
    private final PlayerSkillRepositoryPort playerSkillRepositoryPort;
    private final PlayerRepositoryPort playerRepositoryPort;

    public ManageSkillUseCase(SkillRepositoryPort skillRepositoryPort,
                              PlayerSkillRepositoryPort playerSkillRepositoryPort,
                              PlayerRepositoryPort playerRepositoryPort) {
        this.skillRepositoryPort = skillRepositoryPort;
        this.playerSkillRepositoryPort = playerSkillRepositoryPort;
        this.playerRepositoryPort = playerRepositoryPort;
    }

    public SkillResponse createSkill(CreateSkillRequest request) {
        skillRepositoryPort.findByNombreCanonico(request.nombreCanonico())
                .ifPresent(existing -> {
                    throw new ConflictException("Ya existe una habilidad con el nombre: " + request.nombreCanonico());
                });

        Skill skill = Skill.crear(request.nombreCanonico(), request.descripcion());
        Skill saved = skillRepositoryPort.save(skill);
        return mapToResponse(saved);
    }

    public SkillResponse updateSkill(UUID id, CreateSkillRequest request) {
        Skill skill = skillRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Habilidad no encontrada con ID: " + id));

        skillRepositoryPort.findByNombreCanonico(request.nombreCanonico())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Ya existe otra habilidad con el nombre: " + request.nombreCanonico());
                });

        skill.actualizar(request.nombreCanonico(), request.descripcion(), null);
        Skill saved = skillRepositoryPort.save(skill);
        return mapToResponse(saved);
    }

    public void deleteSkill(UUID id) {
        skillRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Habilidad no encontrada con ID: " + id));

        if (playerSkillRepositoryPort.existsBySkillId(id)) {
            throw new BusinessRuleException("No se puede eliminar la habilidad porque se encuentra asignada a jugadores.");
        }

        skillRepositoryPort.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> listSkills() {
        return skillRepositoryPort.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkillById(UUID id) {
        Skill skill = skillRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Habilidad no encontrada con ID: " + id));
        return mapToResponse(skill);
    }

    public PlayerSkillResponse assignSkillToPlayer(UUID playerId, AssignSkillRequest request) {
        playerRepositoryPort.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Jugador no encontrado con ID: " + playerId));

        Skill skill = skillRepositoryPort.findById(request.habilidadId())
                .orElseThrow(() -> new EntityNotFoundException("Habilidad no encontrada con ID: " + request.habilidadId()));

        if (playerSkillRepositoryPort.findByPlayerIdAndSkillId(playerId, request.habilidadId()).isPresent()) {
            throw new ConflictException("El jugador ya tiene asignada esta habilidad deportiva.");
        }

        PlayerSkill playerSkill = PlayerSkill.crear(playerId, request.habilidadId(), request.nivel(), request.observacion());
        PlayerSkill saved = playerSkillRepositoryPort.save(playerSkill);

        return new PlayerSkillResponse(
                saved.getSkillId(),
                skill.getNombreCanonico(),
                saved.getNivel(),
                saved.getObservacion(),
                saved.getFechaRegistro()
        );
    }

    public void removeSkillFromPlayer(UUID playerId, UUID skillId) {
        playerRepositoryPort.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Jugador no encontrado con ID: " + playerId));

        playerSkillRepositoryPort.findByPlayerIdAndSkillId(playerId, skillId)
                .orElseThrow(() -> new EntityNotFoundException("El jugador no tiene asignada dicha habilidad."));

        playerSkillRepositoryPort.deleteByPlayerIdAndSkillId(playerId, skillId);
    }

    @Transactional(readOnly = true)
    public List<PlayerSkillResponse> getPlayerSkills(UUID playerId) {
        playerRepositoryPort.findById(playerId)
                .orElseThrow(() -> new EntityNotFoundException("Jugador no encontrado con ID: " + playerId));

        return playerSkillRepositoryPort.findByPlayerId(playerId).stream().map(ps -> {
            Skill s = skillRepositoryPort.findById(ps.getSkillId()).orElse(null);
            return new PlayerSkillResponse(
                    ps.getSkillId(),
                    s != null ? s.getNombreCanonico() : "Habilidad",
                    ps.getNivel(),
                    ps.getObservacion(),
                    ps.getFechaRegistro()
            );
        }).toList();
    }

    private SkillResponse mapToResponse(Skill s) {
        return new SkillResponse(
                s.getId(),
                s.getNombreCanonico(),
                s.getDescripcion(),
                s.isActivo()
        );
    }
}
