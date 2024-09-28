package com.community_programmers.learning_platform.domain.services;

import com.community_programmers.learning_platform.domain.User;
import com.community_programmers.learning_platform.domain.UserRolesEnum;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public interface DomainAuthService {
    User register(String username, String email, String password, UserRolesEnum role, PasswordEncoder encoder);
}
