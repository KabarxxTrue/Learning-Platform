package com.community_programmers.learning_platform.application.services;

import com.community_programmers.learning_platform.application.dto.AuthResponse;
import com.community_programmers.learning_platform.application.dto.SignInRequest;
import com.community_programmers.learning_platform.application.dto.SignUpRequest;

public interface AuthService {
    AuthResponse register(SignUpRequest request);
    AuthResponse login(SignInRequest request);
}
