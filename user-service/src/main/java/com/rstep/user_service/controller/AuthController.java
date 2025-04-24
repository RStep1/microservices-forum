package com.rstep.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rstep.user_service.security.jwt.JWTService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JWTService jwtService;

    @GetMapping(value = "/validate")
    public ResponseEntity<Long> validateJwtToken(@RequestHeader("Authorization") String headerAuth) {
        log.debug("Trying to validate token {}", headerAuth);
        String token = jwtService.parseTokenFromHeader(headerAuth);
        Long userId = jwtService.getUserIdFromToken(token);
        return ResponseEntity.ok(userId);
    }
}
