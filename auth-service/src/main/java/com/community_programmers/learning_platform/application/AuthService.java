package com.community_programmers.learning_platform.application;

import com.community_programmers.learning_platform.application.requests.SignInRequest;
import com.community_programmers.learning_platform.application.requests.SignUpRequest;
import com.community_programmers.learning_platform.domain.User;

public interface AuthService {
    User register(SignUpRequest request);
    User login(SignInRequest request);
}
