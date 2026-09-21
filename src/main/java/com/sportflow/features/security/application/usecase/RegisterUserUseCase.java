package com.sportflow.features.security.application.usecase;

import com.sportflow.core.errors.ConflictException;
import com.sportflow.features.security.application.dto.RegisterUserCommand;
import com.sportflow.features.security.application.dto.RegisterUserResponse;
import com.sportflow.features.security.domain.model.Person;
import com.sportflow.features.security.domain.model.Role;
import com.sportflow.features.security.domain.model.User;
import com.sportflow.features.security.domain.ports.PasswordEncoderPort;
import com.sportflow.features.security.domain.ports.RoleRepositoryPort;
import com.sportflow.features.security.domain.ports.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;

    public RegisterUserUseCase(UserRepositoryPort userRepository,
                               RoleRepositoryPort roleRepository,
                               PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterUserResponse execute(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email().trim().toLowerCase())) {
            throw new ConflictException("EMAIL_ALREADY_REGISTERED", "El correo electrónico ya se encuentra registrado en la plataforma.");
        }

        if (userRepository.existsByUsername(command.nombreUsuario().trim())) {
            throw new ConflictException("USERNAME_ALREADY_TAKEN", "El nombre de usuario ya se encuentra en uso.");
        }

        Person persona = Person.crear(
                command.tipoDocumento(),
                command.numeroDocumento(),
                command.nombres(),
                command.apellidos(),
                command.email().trim().toLowerCase(),
                command.telefono()
        );

        String hashedPassword = passwordEncoder.encode(command.password());
        User usuario = User.crearLocal(
                persona,
                command.nombreUsuario().trim(),
                command.email().trim().toLowerCase(),
                hashedPassword
        );

        // Asignar rol por defecto AFICIONADO si existe
        Optional<Role> defaultRole = roleRepository.findByNombre("AFICIONADO");
        defaultRole.ifPresent(usuario::asignarRol);

        User savedUser = userRepository.save(usuario);

        return new RegisterUserResponse(
                savedUser.getId(),
                savedUser.getPersona().getId(),
                savedUser.getNombreUsuario(),
                savedUser.getEmail(),
                savedUser.getEstado().name(),
                savedUser.isDosFactoresHabilitado(),
                "Usuario registrado exitosamente. Ya puede iniciar sesión con sus credenciales."
        );
    }
}
