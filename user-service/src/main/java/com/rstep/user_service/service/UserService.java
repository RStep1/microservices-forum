package com.rstep.user_service.service;

import java.util.List;

import javax.security.sasl.AuthenticationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rstep.user_service.dto.UpdateUserProfileRequest;
import com.rstep.user_service.dto.UserDto;
import com.rstep.user_service.dto.auth.UserCredentialDto;
import com.rstep.user_service.dto.auth.UserRegistrationRequest;
import com.rstep.user_service.exception.AuthenticationFailedException;
import com.rstep.user_service.exception.DataConflictException;
import com.rstep.user_service.exception.EmailExistsException;
import com.rstep.user_service.exception.IncorrectEmailException;
import com.rstep.user_service.exception.IncorrectUsernameException;
import com.rstep.user_service.exception.UserDeletionException;
import com.rstep.user_service.exception.UserServiceException;
import com.rstep.user_service.model.User;
import com.rstep.user_service.repository.UserRepository;
import com.rstep.user_service.security.jwt.JWTService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDto registerUser(UserRegistrationRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            log.error("Trying to register user with existing username");
            throw new IncorrectUsernameException("This username already registered, please try again.");
        }

        if (userRepository.existsByEmail(request.email())) {
            log.error("Trying to register user with existing email");
            throw new IncorrectEmailException("This email already registered, please try again.");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        log.info("Perform user registration");
        return UserDto.from(userRepository.save(user));
    }

    public String verify(UserCredentialDto userCredential) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userCredential.username(), userCredential.password())
            );

            User user = userRepository.findByUsername(userCredential.username())
                .orElseThrow(() -> new AuthenticationException("User not found"));

            return jwtService.generateToken(user);

        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Invalid credentials");
        } catch (Exception e) {
            throw new AuthenticationFailedException("Authentication failed");
        }
    }

    public UserDto readUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return UserDto.from(user);
    }

    public List<UserDto> readUsers() {
        return userRepository.findAll().stream().map(user -> UserDto.from(user)).toList();
    }

    public UserDto updateUserProfile(Long id, UpdateUserProfileRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                log.error("User not found with id: {}", id);
                return new EntityNotFoundException("User not found with id: " + id);
            });
        
        if (!user.getEmail().equals(request.email())) {
            if (userRepository.existsByEmail(request.email())) {
                log.error("Duplicate email detected: {}", request.email());
                throw new EmailExistsException("Email '" + request.email() + "' is already in use");
            }
        }
        
        try {
            user.setEmail(request.email());
            return UserDto.from(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while updating user {}: {}", id, e.getMessage());
            throw new DataConflictException("Failed to update user due to data conflict", e);
        } catch (Exception e) {
            log.error("Unexpected error updating user {}: {}", id, e.getMessage());
            throw new UserServiceException("Failed to update user profile", e);
        }
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            log.error("User not found with id: {}", id);
            throw new EntityNotFoundException("User not found with id: " + id);
        }

        try {
            userRepository.deleteById(id);
            log.info("User with id {} was successfully deleted", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while deleting user {}: {}", id, e.getMessage());
            throw new UserDeletionException("Cannot delete user due to existing references");
        } catch (JpaSystemException | PersistenceException e) {
            log.error("Unexpected error deleting user {}: {}", id, e.getMessage());
            throw new UserServiceException("Failed to delete user due to persistence error", e);
        }
    }


}
