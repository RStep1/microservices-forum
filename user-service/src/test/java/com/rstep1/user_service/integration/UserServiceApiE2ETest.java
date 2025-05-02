package com.rstep1.user_service.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
import com.rstep1.user_service.dto.auth.UserCredentialDto;
import com.rstep1.user_service.dto.auth.UserRegistrationRequest;
import com.rstep1.user_service.repository.UserRepository;
import com.rstep1.user_service.util.TestUserUtils;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ContextConfiguration(initializers = TestcontainersInitializer.class)
public class UserServiceApiE2ETest {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldCreateUser() throws Exception {
        UserRegistrationRequest userRegistrationRequest = TestUserUtils.getUserRegistrationRequest();
        String requestString = objectMapper.writeValueAsString(userRegistrationRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user-service/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestString))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Disabled
    @Test
    public void shouldCreateJwtToken() throws Exception {
        // add registration before


        UserCredentialDto userCredentialDto = TestUserUtils.getUserCredentialDto();
        String requestString = objectMapper.writeValueAsString(userCredentialDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user-service/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestString))
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }
}
