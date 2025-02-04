package com.community_programmers.learning_platform.application.services;

import com.community_programmers.learning_platform.application.dto.AuthResponse;
import com.community_programmers.learning_platform.application.dto.SignInRequest;
import com.community_programmers.learning_platform.application.dto.SignUpRequest;
import com.community_programmers.learning_platform.application.security.JwtService;
import com.community_programmers.learning_platform.domain.User;
import com.community_programmers.learning_platform.infrastructure.UserRepository;
import com.community_programmers.learning_platform.domain.services.DomainAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final DomainAuthService domainAuthService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(SignUpRequest request) {
        if (containsSqlInjection(request.getUsername()))
            throw new BadCredentialsException("Invalid username or email.");

        if (userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail()).isPresent())
            throw new BadCredentialsException("User already exists with this username or email.");

        User newUser = domainAuthService.register(request.getUsername(), request.getEmail(),
                request.getPassword(), request.getRole(), passwordEncoder);
        log.debug("Registering new user {}", request.getUsername());

        User savedUser = userRepository.save(newUser);
        log.debug("Saving user: {}", savedUser);

        String token = jwtService.generateToken(savedUser.getUsername());
        log.debug("Generated token for register: {}", token);

        return new AuthResponse(savedUser, token);
    }

    @Override
    public AuthResponse login(SignInRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or email."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Something went wrong. Try again.");

        String token = jwtService.generateToken(user.getUsername());
        log.debug("Generated token for login: {}", token);

        return new AuthResponse(user, token);
    }

    private boolean containsSqlInjection(String input) {
        return input != null && (input.contains("'") || input.toLowerCase().contains("or"));
    }
}
