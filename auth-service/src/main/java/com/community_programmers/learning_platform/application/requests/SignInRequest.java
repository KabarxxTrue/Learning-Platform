package com.community_programmers.learning_platform.application.requests;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {
    @Email
    private String email;
    private String password;
}
