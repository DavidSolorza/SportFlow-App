package com.sportflow.features.security.domain.ports;

import com.sportflow.features.security.domain.model.AuthProvider;
import com.sportflow.features.security.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByProvider(AuthProvider provider, String providerId);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    User save(User user);
    List<User> findAll(int page, int size);
    long count();
    void delete(UUID id);
}
