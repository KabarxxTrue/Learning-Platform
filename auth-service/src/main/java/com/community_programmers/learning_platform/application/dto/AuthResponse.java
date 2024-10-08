package com.community_programmers.learning_platform.application.dto;

import com.community_programmers.learning_platform.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
    private User user;
    private String token;
}
