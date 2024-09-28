package com.community_programmers.learning_platform.application.requests;

import com.community_programmers.learning_platform.domain.UserRolesEnum;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    private String username;
    private String password;
    @Email
    private String email;
    private UserRolesEnum role;
}
