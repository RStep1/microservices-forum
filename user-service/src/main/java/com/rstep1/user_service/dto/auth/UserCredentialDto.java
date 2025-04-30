package com.rstep1.user_service.dto.auth;

import lombok.Builder;

@Builder
public record UserCredentialDto(String username, String password) {
}
