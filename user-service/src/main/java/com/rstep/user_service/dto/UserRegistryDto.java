package com.rstep.user_service.dto;

import com.rstep.user_service.model.User;

public record UserRegistryDto(String username, String email, String password) {
    public static UserRegistryDto from(User user) {
        return new UserRegistryDto(user.getUsername(), user.getEmail(), user.getPassword());
    }
}
