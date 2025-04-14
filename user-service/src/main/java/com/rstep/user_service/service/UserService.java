package com.rstep.user_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rstep.user_service.dto.UserCredentialDto;
import com.rstep.user_service.dto.UserRegistryDto;
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

    public UserRegistryDto registerUser(UserRegistryDto userRegistryDto) {

        if (userRepository.existsByUsername(userRegistryDto.username())) {
            log.error("Trying to register user with existing username");
            throw new IncorrectUsernameException("This username already registered, please try again.");
        }

        if (userRepository.existsByEmail(userRegistryDto.email())) {
            log.error("Trying to register user with existing email");
            throw new IncorrectEmailException("This email already registered, please try again.");
        }

        User user = new User();
        user.setUsername(userRegistryDto.username());
        user.setEmail(userRegistryDto.email());
        user.setPassword(passwordEncoder.encode(userRegistryDto.password()));

        log.info("Perform user registration");
        return UserRegistryDto.from(userRepository.save(user));
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
