package com.community_programmers.learning_platform.ui;

import com.community_programmers.learning_platform.application.requests.SignInRequest;
import com.community_programmers.learning_platform.application.requests.SignUpRequest;
import com.community_programmers.learning_platform.application.services.AuthService;
import com.community_programmers.learning_platform.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody SignUpRequest request) {
        User registeredUser = authService.register(request);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody SignInRequest request) {
        User loggedInUser = authService.login(request);
        return ResponseEntity.ok(loggedInUser);
    }
}
