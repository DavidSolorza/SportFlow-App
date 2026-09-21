package com.sportflow.features.sports;

import com.sportflow.core.errors.ConflictException;
import com.sportflow.core.errors.EntityNotFoundException;
import com.sportflow.features.sports.application.dto.CreateSportRequest;
import com.sportflow.features.sports.application.dto.SportHierarchyResponse;
import com.sportflow.features.sports.application.dto.SportResponse;
import com.sportflow.features.sports.application.usecases.ManageSportUseCase;
import com.sportflow.features.sports.domain.model.Sport;
import com.sportflow.features.sports.domain.ports.SportRepositoryPort;
import com.sportflow.features.sports.domain.ports.TeamRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SportUseCaseTest {

    private SportRepositoryPort sportRepository;
    private TeamRepositoryPort teamRepository;
    private ManageSportUseCase useCase;

    @BeforeEach
    void setUp() {
        sportRepository = Mockito.mock(SportRepositoryPort.class);
        teamRepository = Mockito.mock(TeamRepositoryPort.class);
        useCase = new ManageSportUseCase(sportRepository, teamRepository);
    }

    @Test
    @DisplayName("HU-GD-01: Debe registrar una disciplina deportiva correctamente")
    void shouldCreateSportSuccessfully() {
        CreateSportRequest request = new CreateSportRequest("Fútbol", "Deporte colectivo", null);

        when(sportRepository.findByNombreCanonico("Fútbol")).thenReturn(Optional.empty());
        when(sportRepository.save(any(Sport.class))).thenAnswer(inv -> inv.getArgument(0));

        SportResponse response = useCase.createSport(request);

        assertNotNull(response);
        assertEquals("Fútbol", response.nombreCanonico());
        assertTrue(response.activo());
        verify(sportRepository).save(any(Sport.class));
    }

    @Test
    @DisplayName("HU-GD-01: Debe rechazar disciplina con nombre duplicado")
    void shouldRejectDuplicateSportName() {
        CreateSportRequest request = new CreateSportRequest("Fútbol", "Deporte colectivo", null);
        when(sportRepository.findByNombreCanonico("Fútbol"))
                .thenReturn(Optional.of(Sport.crear("Fútbol", "Existente", null)));

        assertThrows(ConflictException.class, () -> useCase.createSport(request));
        verify(sportRepository, never()).save(any(Sport.class));
    }

    @Test
    @DisplayName("HU-GD-01: Debe construir la jerarquía recursiva de disciplinas y subdeportes")
    void shouldReturnSportHierarchyRecursively() {
        Sport padre = Sport.crear("Fútbol", "Fútbol base", null);
        Sport subdeporte = Sport.crear("Fútbol Sala", "Futsal de pista", padre.getId());

        when(sportRepository.findById(padre.getId())).thenReturn(Optional.of(padre));
        when(sportRepository.findByDeportePadreId(padre.getId())).thenReturn(List.of(subdeporte));
        when(sportRepository.findByDeportePadreId(subdeporte.getId())).thenReturn(List.of());

        SportHierarchyResponse hierarchy = useCase.getSportHierarchy(padre.getId());

        assertNotNull(hierarchy);
        assertEquals("Fútbol", hierarchy.nombreCanonico());
        assertEquals(1, hierarchy.subdeportes().size());
        assertEquals("Fútbol Sala", hierarchy.subdeportes().get(0).nombreCanonico());
    }

    @Test
    @DisplayName("HU-GD-01: Debe lanzar EntityNotFoundException si la disciplina no existe")
    void shouldThrowWhenSportNotFound() {
        UUID randomId = UUID.randomUUID();
        when(sportRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> useCase.getSportById(randomId));
    }
}
