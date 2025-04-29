package com.rstep1.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UserServiceIntegrationTests extends AbstractIntegrationTest {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateUser() throws Exception {
        UserRegistrationRequest userRegistrationRequest = getUserRegistrationRequest();
        String requestString = objectMapper.writeValueAsString(userRegistrationRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user-service/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestString))
            .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    private UserRegistrationRequest getUserRegistrationRequest() {
        return UserRegistrationRequest.builder()
            .username("usernametest")
            .email("emailtest")
            .password("passwordtest").build();
    }
}
