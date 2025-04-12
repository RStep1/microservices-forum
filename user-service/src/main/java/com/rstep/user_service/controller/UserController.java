package com.rstep.user_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rstep.user_service.dto.UserRegistryDto;
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
    public ResponseEntity<UserRegistryDto> createUser(@RequestBody UserRegistryDto request) {
        log.info("Creating user with {}", request.toString());
        return ResponseEntity.ok(userService.registerUser(request));
    }
}
