package com.community_programmers.learning_platform.domain.services;

import com.community_programmers.learning_platform.domain.User;
import com.community_programmers.learning_platform.domain.UserRolesEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DomainAuthServiceImpl implements DomainAuthService {

    @Override
    public User register(String username, String email, String password, UserRolesEnum role, PasswordEncoder encoder) {
        return User.createNewUser(email, username, password, role, encoder);
    }
}
