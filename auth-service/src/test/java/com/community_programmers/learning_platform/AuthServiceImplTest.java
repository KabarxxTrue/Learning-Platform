package com.community_programmers.learning_platform;

import com.community_programmers.learning_platform.application.dto.AuthResponse;
import com.community_programmers.learning_platform.application.dto.SignInRequest;
import com.community_programmers.learning_platform.application.dto.SignUpRequest;
import com.community_programmers.learning_platform.application.security.JwtService;
import com.community_programmers.learning_platform.application.services.AuthServiceImpl;
import com.community_programmers.learning_platform.domain.User;
import com.community_programmers.learning_platform.domain.UserRolesEnum;
import com.community_programmers.learning_platform.domain.services.DomainAuthService;
import com.community_programmers.learning_platform.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private DomainAuthService domainAuthService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldRegisterNewUserSuccessfully() {
        SignUpRequest request = new SignUpRequest("user123", "user@example.com", "password", UserRolesEnum.USER);
        User newUser = new User("user123", "encodedPassword", "user@example.com", UserRolesEnum.USER);

        when(userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail()))
                .thenReturn(Optional.empty());
        when(domainAuthService.register(request.getUsername(), request.getEmail(),
                request.getPassword(), request.getRole(), passwordEncoder)).thenReturn(newUser);
        when(userRepository.save(newUser)).thenReturn(newUser);
        when(jwtService.generateToken(newUser.getUsername())).thenReturn("jwtToken");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("user123", response.getUser().getUsername());
        assertEquals("jwtToken", response.getToken());

        verify(userRepository).findByUsernameOrEmail(request.getUsername(), request.getEmail());
        verify(userRepository).save(newUser);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        SignUpRequest request = new SignUpRequest("user123", "user@example.com", "password", UserRolesEnum.USER);

        when(userRepository.findByUsernameOrEmail(request.getUsername(), request.getEmail()))
                .thenReturn(Optional.of(new User()));

        assertThrows(BadCredentialsException.class, () -> authService.register(request));

        verify(userRepository).findByUsernameOrEmail(request.getUsername(), request.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldLoginSuccessfully() {
        SignInRequest request = new SignInRequest("user@example.com", "password");
        User user = new User("user123", "encodedPassword", "user@example.com", UserRolesEnum.USER);

        when(userRepository.findByEmail(request.getUsername()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(true);
        when(jwtService.generateToken(user.getUsername()))
                .thenReturn("jwtToken");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("user123", response.getUser().getUsername());
        assertEquals("jwtToken", response.getToken());

        verify(userRepository).findByEmail(request.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        SignInRequest request = new SignInRequest("user@example.com", "wrongPassword");
        User user = new User("user123", "encodedPassword", "user@example.com", UserRolesEnum.USER);

        when(userRepository.findByEmail(request.getUsername()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(userRepository).findByEmail(request.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        SignInRequest request = new SignInRequest("user@example.com", "password");

        when(userRepository.findByEmail(request.getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(userRepository).findByEmail(request.getUsername());
    }
}
