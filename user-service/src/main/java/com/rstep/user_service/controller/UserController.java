package com.rstep.user_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rstep.user_service.dto.ErrorResponse;
import com.rstep.user_service.dto.UserDto;
import com.rstep.user_service.dto.auth.JWTAuthenticationResponse;
import com.rstep.user_service.dto.auth.UserCredentialDto;
import com.rstep.user_service.dto.auth.UserRegistrationRequest;
import com.rstep.user_service.exception.AuthenticationFailedException;
import com.rstep.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/user-service")
@RequiredArgsConstructor
public class UserController {
    
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

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<UserDto> readUser(@PathVariable("id") Long id) {
        log.info("Reading user by id {}", id);
        return ResponseEntity.ok(userService.readUser(id));
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<UserDto>> readUsers() {
        log.info("Reading all users");
        return ResponseEntity.ok(userService.readUsers());
    }

}
