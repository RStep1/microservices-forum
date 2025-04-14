package com.rstep.user_service.dto.auth;

import com.rstep.user_service.model.User;

public record UserRegistrationResponse(Long id, String username, String email) {
    public static UserRegistrationResponse from(User user) {
        return new UserRegistrationResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
