package com.sportflow.features.sports.infrastructure.persistence.adapters;

import com.sportflow.features.sports.domain.model.Skill;
import com.sportflow.features.sports.domain.ports.SkillRepositoryPort;
import com.sportflow.features.sports.infrastructure.persistence.entities.SkillEntity;
import com.sportflow.features.sports.infrastructure.persistence.repositories.SpringDataSkillRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaSkillRepositoryAdapter implements SkillRepositoryPort {

    private final SpringDataSkillRepository repository;

    public JpaSkillRepositoryAdapter(SpringDataSkillRepository repository) {
        this.repository = repository;
    }

    @Override
    public Skill save(Skill skill) {
        SkillEntity entity = new SkillEntity(
                skill.getId(),
                skill.getNombreCanonico(),
                skill.getDescripcion(),
                skill.isActivo(),
                skill.getCreadoEn()
        );
        SkillEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Skill> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Skill> findByNombreCanonico(String nombreCanonico) {
        return repository.findByNombreCanonico(nombreCanonico).map(this::toDomain);
    }

    @Override
    public List<Skill> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Skill> findByActivo(boolean activo) {
        return repository.findByActivo(activo).stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Skill toDomain(SkillEntity entity) {
        return new Skill(
                entity.getId(),
                entity.getNombreCanonico(),
                entity.getDescripcion(),
                entity.isActivo(),
                entity.getCreadoEn()
        );
    }
}
