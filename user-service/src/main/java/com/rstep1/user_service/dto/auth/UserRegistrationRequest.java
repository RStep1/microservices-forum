package com.rstep1.user_service.dto.auth;

import com.rstep1.user_service.model.User;

public record UserRegistrationRequest(String username, String email, String password) {
    public static UserRegistrationRequest from(User user) {
        return new UserRegistrationRequest(user.getUsername(), user.getEmail(), user.getPassword());
    }
}
