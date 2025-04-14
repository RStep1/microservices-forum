package com.rstep.user_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rstep.user_service.dto.auth.UserCredentialDto;
import com.rstep.user_service.dto.auth.UserRegistrationRequest;
import com.rstep.user_service.dto.auth.UserRegistrationResponse;
import com.rstep.user_service.exception.AuthenticationFailedException;
import com.rstep.user_service.exception.IncorrectEmailException;
import com.rstep.user_service.exception.IncorrectUsernameException;
import com.rstep.user_service.model.User;
import com.rstep.user_service.repository.UserRepository;
import com.rstep.user_service.security.jwt.JWTService;

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

    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {

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
        return UserRegistrationResponse.from(userRepository.save(user));
    }

    public String verify(UserCredentialDto userCredential) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userCredential.username(), userCredential.password())
            );
            return jwtService.generateToken(userCredential.username());

        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Invalid credentials");
        } catch (Exception e) {
            throw new AuthenticationFailedException("Authentication failed");
        }
    }
}
