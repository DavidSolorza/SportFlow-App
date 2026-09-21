package com.sportflow.features.sports.application.usecases;

import com.sportflow.core.errors.BusinessRuleException;
import com.sportflow.core.errors.ConflictException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.sports.application.dto.CreateSportRequest;
import com.sportflow.features.sports.application.dto.SportHierarchyResponse;
import com.sportflow.features.sports.application.dto.SportResponse;
import com.sportflow.features.sports.application.dto.UpdateSportRequest;
import com.sportflow.features.sports.domain.model.Sport;
import com.sportflow.features.sports.domain.ports.SportRepositoryPort;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ManageSportUseCase {

    private final SportRepositoryPort sportRepositoryPort;
    private final TeamRepositoryPort teamRepositoryPort;

    public ManageSportUseCase(SportRepositoryPort sportRepositoryPort, TeamRepositoryPort teamRepositoryPort) {
        this.sportRepositoryPort = sportRepositoryPort;
        this.teamRepositoryPort = teamRepositoryPort;
    }

    public SportResponse createSport(CreateSportRequest request) {
        if (sportRepositoryPort.findByNombreCanonico(request.nombreCanonico()).isPresent()) {
            throw new ConflictException("Ya existe un deporte registrado con el nombre: " + request.nombreCanonico());
        }

        if (request.deportePadreId() != null) {
            sportRepositoryPort.findById(request.deportePadreId())
                    .orElseThrow(() -> new EntityNotFoundException("El deporte padre especificado no existe."));
        }

        Sport sport = Sport.crear(request.nombreCanonico(), request.descripcion(), request.deportePadreId());
        Sport saved = sportRepositoryPort.save(sport);
        return mapToResponse(saved);
    }

    public SportResponse updateSport(UUID id, UpdateSportRequest request) {
        Sport sport = sportRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deporte no encontrado con ID: " + id));

        sportRepositoryPort.findByNombreCanonico(request.nombreCanonico())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Ya existe otro deporte con el nombre: " + request.nombreCanonico());
                });

        if (request.deportePadreId() != null) {
            if (request.deportePadreId().equals(id)) {
                throw new BusinessRuleException("Un deporte no puede ser padre de sí mismo.");
            }
            sportRepositoryPort.findById(request.deportePadreId())
                    .orElseThrow(() -> new EntityNotFoundException("El deporte padre especificado no existe."));
        }

        sport.actualizar(request.nombreCanonico(), request.descripcion(), request.activo(), request.deportePadreId());
        Sport saved = sportRepositoryPort.save(sport);
        return mapToResponse(saved);
    }

    public void deleteOrDeactivateSport(UUID id) {
        Sport sport = sportRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deporte no encontrado con ID: " + id));

        if (sportRepositoryPort.existsByDeportePadreId(id)) {
            throw new BusinessRuleException("No se puede eliminar el deporte porque tiene subdeportes asociados. " +
                    "Reasigne o elimine los subdeportes primero.");
        }

        if (teamRepositoryPort.existsByDeporteId(id)) {
            throw new BusinessRuleException("No se puede eliminar el deporte porque existen equipos que lo practican. " +
                    "Puede proceder a desactivarlo.");
        }

        sportRepositoryPort.deleteById(id);
    }

    @Transactional(readOnly = true)
    public SportResponse getSportById(UUID id) {
        Sport sport = sportRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deporte no encontrado con ID: " + id));
        return mapToResponse(sport);
    }

    @Transactional(readOnly = true)
    public List<SportResponse> listSports(Boolean soloActivos) {
        List<Sport> sports = (soloActivos != null && soloActivos)
                ? sportRepositoryPort.findByActivo(true)
                : sportRepositoryPort.findAll();
        return sports.stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public SportHierarchyResponse getSportHierarchy(UUID id) {
        Sport root = sportRepositoryPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deporte no encontrado con ID: " + id));
        return buildHierarchyRecursive(root);
    }

    private SportHierarchyResponse buildHierarchyRecursive(Sport current) {
        List<Sport> hijos = sportRepositoryPort.findByDeportePadreId(current.getId());
        List<SportHierarchyResponse> subdeportesDTO = new ArrayList<>();
        for (Sport hijo : hijos) {
            subdeportesDTO.add(buildHierarchyRecursive(hijo));
        }
        return new SportHierarchyResponse(
                current.getId(),
                current.getNombreCanonico(),
                current.getDescripcion(),
                current.isActivo(),
                current.getDeportePadreId(),
                subdeportesDTO
        );
    }

    private SportResponse mapToResponse(Sport sport) {
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
