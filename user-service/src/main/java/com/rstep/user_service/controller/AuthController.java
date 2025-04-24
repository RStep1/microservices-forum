package com.rstep.user_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rstep.user_service.dto.ErrorResponse;
import com.rstep.user_service.dto.auth.JWTAuthenticationResponse;
import com.rstep.user_service.dto.auth.UserCredentialDto;
import com.rstep.user_service.dto.auth.UserRegistrationRequest;
import com.rstep.user_service.exception.AuthenticationFailedException;
import com.rstep.user_service.security.jwt.JWTService;
import com.rstep.user_service.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/user-service/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JWTService jwtService;
    private final UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity<?> createUser(@RequestBody UserRegistrationRequest request) {
        try {
            log.info("Creating user with {}", request.toString());
            return ResponseEntity.ok(userService.registerUser(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Failed to sign up", e.getMessage()));
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody UserCredentialDto request) {
        try {
            log.info("Authenticating user with {}", request.toString());
            String token = userService.verify(request);
            JWTAuthenticationResponse response = new JWTAuthenticationResponse(token);
            return ResponseEntity.ok(response);
        } catch (AuthenticationFailedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Failed to sign in", e.getMessage()));
        }
    }

    @GetMapping(value = "/validate")
    public ResponseEntity<?> validateJwtToken(@RequestHeader("Authorization") String headerAuth) {
        try {
            log.debug("Trying to validate token {}", headerAuth);
            String token = jwtService.parseTokenFromHeader(headerAuth);
            return ResponseEntity.ok(jwtService.getUserIdFromToken(token));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid token format: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid token format", e.getMessage()));
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token expired", e.getMessage()));
        } catch (JwtException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid token", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during token validation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", e.getMessage()));
        }
    }
}
