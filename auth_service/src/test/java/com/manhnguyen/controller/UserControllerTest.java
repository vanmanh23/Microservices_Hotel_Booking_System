package com.manhnguyen.controller;

import com.manhnguyen.common.exception.GlobalExceptionHandler;
import com.manhnguyen.dto.UserDTO;
import com.manhnguyen.model.Role;
import com.manhnguyen.service.UserService;
import com.manhnguyen.support.WebMvcTestSecuritySupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class UserControllerTest extends WebMvcTestSecuritySupport {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getProfile_returns200() throws Exception {
        UserDTO profile = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("user@example.com")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        when(userService.getProfile("user@example.com")).thenReturn(profile);

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void updateProfile_returns200() throws Exception {
        UserDTO updated = UserDTO.builder()
                .id(1L)
                .name("Updated Name")
                .email("user@example.com")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        when(userService.updateProfile(org.mockito.ArgumentMatchers.eq("user@example.com"), org.mockito.ArgumentMatchers.any()))
                .thenReturn(updated);

        mockMvc.perform(put("/api/users/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated Name"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }
}
