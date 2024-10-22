package com.community_programmers.learning_platform;

import com.community_programmers.learning_platform.application.dto.SignInRequest;
import com.community_programmers.learning_platform.application.dto.SignUpRequest;
import com.community_programmers.learning_platform.application.security.JwtService;
import com.community_programmers.learning_platform.application.services.AuthServiceImpl;
import com.community_programmers.learning_platform.domain.UserRolesEnum;
import com.community_programmers.learning_platform.domain.services.DomainAuthService;
import com.community_programmers.learning_platform.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class AuthServiceSecurityTest {

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

    public AuthServiceSecurityTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldNotAllowSqlInjectionInRegister() {
        SignUpRequest request = new SignUpRequest("user' OR '1'='1", "user@example.com", "password", UserRolesEnum.USER);

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class, () -> authService.register(request));

        assertEquals("Invalid username or email.", thrown.getMessage());
    }

    @Test
    void shouldNotAllowSqlInjectionInLogin() {
        SignInRequest request = new SignInRequest("user' OR '1'='1", "password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BadCredentialsException thrown = assertThrows(BadCredentialsException.class, () -> authService.login(request));

        assertEquals("Invalid username or email.", thrown.getMessage());

        verify(userRepository).findByEmail(request.getUsername());
    }
}
