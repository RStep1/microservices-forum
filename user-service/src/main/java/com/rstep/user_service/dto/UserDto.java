package com.rstep.user_service.dto;

import com.rstep.user_service.model.User;

public record UserDto(Long id, String username, String email) {
    public static UserDto from(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
