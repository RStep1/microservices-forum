package com.rstep1.user_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rstep1.user_service.dto.UpdateUserProfileRequest;
import com.rstep1.user_service.dto.UserDto;
import com.rstep1.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/user-service")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @GetMapping(value = "/users/{id}")
    public ResponseEntity<UserDto> readUser(@PathVariable("id") Long id) {
        log.info("Reading user by id {}", id);
        return ResponseEntity.ok(userService.readUser(id));
    }

    @GetMapping(value = "/users")
    public ResponseEntity<List<UserDto>> readUsers() {
        log.info("Reading all users");
        return ResponseEntity.ok(userService.readUsers());
    }

    @PutMapping(value = "/users/{id}")
    @PreAuthorize("#id == principal.id")
    public ResponseEntity<UserDto> updateUserProfile(
                @PathVariable("id") Long id,
                @RequestBody UpdateUserProfileRequest request
            ) {
        log.info("Updating user profile with {}", request.toString());
        return ResponseEntity.ok(userService.updateUserProfile(id, request));
    }

    @DeleteMapping(value = "/users/{id}")
    @PreAuthorize("#id == principal.id")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        log.info("Deleting user profile with id {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
