package com.community_programmers.learning_platform.application.services;

import com.community_programmers.learning_platform.application.requests.SignInRequest;
import com.community_programmers.learning_platform.application.requests.SignUpRequest;
import com.community_programmers.learning_platform.domain.User;
import com.community_programmers.learning_platform.infrastructure.UserRepository;
import com.community_programmers.learning_platform.domain.services.DomainAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final DomainAuthService domainAuthService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(SignUpRequest request) {

        if (userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail()).isPresent())
            throw new BadCredentialsException("User already exists with this username or email.");

        User newUser = domainAuthService.register(request.getUsername(), request.getEmail(),
                request.getPassword(), request.getRole(), passwordEncoder);

        return userRepository.save(newUser);
    }

    @Override
    public User login(SignInRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or email."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Something went wrong. Try again.");

        return user;
    }
}
