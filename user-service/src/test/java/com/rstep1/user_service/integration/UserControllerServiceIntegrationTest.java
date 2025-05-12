package com.rstep1.user_service.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rstep1.user_service.config.auth.WithMockUserPrincipal;
import com.rstep1.user_service.dto.UpdateUserProfileRequest;
import com.rstep1.user_service.dto.UserDto;
import com.rstep1.user_service.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerServiceIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    public void givenValidUserId_whenGetUserById_thenReturnUserDto() throws Exception {
        Long userId = 1L;
        UserDto mockUser = new UserDto(userId, "testuser", "test@example.com");
        
        when(userService.readUser(userId)).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user-service/users/{id}", userId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testuser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = {"ROLE_USER"})
    public void whenGetAllUsers_thenReturnUserList() throws Exception {
        List<UserDto> mockUsers = List.of(
            new UserDto(1L, "user1", "user1@example.com"),
            new UserDto(2L, "user2", "user2@example.com")
        );
        when(userService.readUsers()).thenReturn(mockUsers);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user-service/users"))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
               .andExpect(MockMvcResultMatchers.jsonPath("$[0].username").value("user1"))
               .andExpect(MockMvcResultMatchers.jsonPath("$[1].username").value("user2"));
    }

    @Test
    @WithMockUserPrincipal(id = 1L, username = "testuser", password = "password", authorities = {"ROLE_USER"})
    public void givenValidUpdateRequest_whenUpdateUser_thenReturnUpdatedUser() throws Exception {
        Long userId = 1L;    

        UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest("new@example.com");
        UserDto updatedUser = new UserDto(userId, "testuser", "new@example.com");
        
        when(userService.updateUserProfile(eq(userId), any(UpdateUserProfileRequest.class)))
            .thenReturn(updatedUser);

        String requestString = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/user-service/users/{id}", userId)
               .contentType(MediaType.APPLICATION_JSON)
               .content(requestString))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @WithMockUserPrincipal(id = 1L, username = "testuser", password = "password", authorities = {"ROLE_USER"})
    public void givenValidUserId_whenDeleteUser_thenReturnNoContent() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/user-service/users/{id}", userId))
               .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
