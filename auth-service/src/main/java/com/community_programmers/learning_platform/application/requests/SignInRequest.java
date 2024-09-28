package com.community_programmers.learning_platform.application.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInRequest {
    private String username;
    private String password;
}
