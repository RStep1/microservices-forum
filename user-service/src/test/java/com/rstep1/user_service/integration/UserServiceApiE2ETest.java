package com.rstep1.user_service.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rstep1.user_service.config.TestcontainersInitializer;
import com.rstep1.user_service.dto.auth.JWTAuthenticationResponse;
import com.rstep1.user_service.dto.auth.UserCredentialDto;
import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.security.jwt.JWTService;
import com.rstep1.user_service.util.TestUserUtils;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ContextConfiguration(initializers = TestcontainersInitializer.class)
public class UserServiceApiE2ETest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTService jwtService;

    private String authToken;
    private UserRegistrationRequest testUser;
    private Long userId;

    @BeforeEach
    public void setup() throws Exception {
        testUser = TestUserUtils.createRandomRegistrationRequest();
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user-service/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andReturn().getResponse().getContentAsString();
        
        UserCredentialDto loginCredentials = TestUserUtils.createUserCredentialDto(
            testUser.username(),
            testUser.password()
        );
        
        String loginResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user-service/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginCredentials)))
                .andReturn().getResponse().getContentAsString();
        
        JWTAuthenticationResponse jwtResponse = objectMapper.readValue(loginResponse, JWTAuthenticationResponse.class);
        authToken = jwtResponse.jwtToken();
        userId = jwtService.getUserIdFromToken(authToken);
    }

    @Test
    public void givenValidRegistrationRequest_whenRegisterUser_thenReturnSuccess() throws Exception {
        UserRegistrationRequest newUser = TestUserUtils.createRandomRegistrationRequest();
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user-service/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.username").value(newUser.username()))
                .andExpect(jsonPath("$.email").value(newUser.email()));
    }

    @Test
    public void givenDuplicateUsername_whenRegisterUser_thenReturnConflict() throws Exception {
        UserRegistrationRequest duplicateRequest = TestUserUtils.createRegistrationRequest(
            testUser.username(),
            "different@example.com",
            "DifferentPassword123!"
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user-service/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(jsonPath("$.message")
                .value("This username already registered, please try again."));
    }

    @Test
    public void givenValidCredentials_whenLogin_thenReturnJwtToken() throws Exception {
        UserCredentialDto credentials = TestUserUtils.createUserCredentialDto(
            testUser.username(),
            testUser.password()
        );
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user-service/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.jwtToken").isNotEmpty());
    }

    @Test
    public void givenInvalidCredentials_whenLogin_thenReturnUnauthorized() throws Exception {
        UserCredentialDto credentials = TestUserUtils.createUserCredentialDto(
            testUser.username(),
            "wrongPassword"
        );
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user-service/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
    
    @Test
    public void givenAuthenticatedUser_whenGetUserProfile_thenReturnUserData() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user-service/users/{id}", userId)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value(testUser.username()))
                .andExpect(jsonPath("$.email").value(testUser.email()));
    }

    @Test
    public void givenValidUpdateRequest_whenUpdateProfile_thenReturnUpdatedUser() throws Exception {
        String newEmail = "updated_" + testUser.email();
        // String requestJson = "{ \"email\": \"" + newEmail + "\" }";
        String requestJson = objectMapper.writeValueAsString(newEmail);
        
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user-service/users/{id}", userId)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail));
    }

    @Test
    public void givenAuthenticatedUser_whenDeleteProfile_thenReturnNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user-service/users/{id}", userId)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}