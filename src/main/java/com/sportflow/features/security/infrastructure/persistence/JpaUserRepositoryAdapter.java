package com.sportflow.features.security.infrastructure.persistence;

import com.sportflow.features.security.domain.model.AuthProvider;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.ports.UserRepositoryPort;
import com.sportflow.features.security.infrastructure.persistence.repositories.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final SpringDataUsuarioRepository usuarioRepository;
    private final SpringDataPersonaRepository personaRepository;
    private final SpringDataPerfilRepository perfilRepository;

    public JpaUserRepositoryAdapter(SpringDataUsuarioRepository usuarioRepository,
                                    SpringDataPersonaRepository personaRepository,
                                    SpringDataPerfilRepository perfilRepository) {
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.perfilRepository = perfilRepository;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return usuarioRepository.findById(id).map(this::enrichAndMap);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return usuarioRepository.findByEmail(email).map(this::enrichAndMap);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return usuarioRepository.findByNombreUsuario(username).map(this::enrichAndMap);
    }

    @Override
    public Optional<User> findByProvider(AuthProvider provider, String providerId) {
        return usuarioRepository.findByProveedorAuthAndProveedorId(provider.name(), providerId).map(this::enrichAndMap);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByNombreUsuario(username);
    }

    @Override
    public User save(User user) {
        UsuarioJpaEntity entity = SecurityEntityMapper.toEntity(user);
        UsuarioJpaEntity savedEntity = usuarioRepository.save(entity);

        PerfilJpaEntity perfilEntity = null;
        if (user.getPerfil() != null) {
            PerfilJpaEntity pEntity = SecurityEntityMapper.toEntity(user.getPerfil());
            perfilEntity = perfilRepository.save(pEntity);
        }

        return SecurityEntityMapper.toDomain(savedEntity, perfilEntity);
    }

    @Override
    public List<User> findAll(int page, int size) {
        return usuarioRepository.findAll(PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(this::enrichAndMap)
                .toList();
    }

    @Override
    public long count() {
        return usuarioRepository.count();
    }

    @Override
    public void delete(UUID id) {
        usuarioRepository.deleteById(id);
    }

    private User enrichAndMap(UsuarioJpaEntity entity) {
        PerfilJpaEntity perfil = perfilRepository.findByUsuarioId(entity.getId()).orElse(null);
        return SecurityEntityMapper.toDomain(entity, perfil);
    }
}
