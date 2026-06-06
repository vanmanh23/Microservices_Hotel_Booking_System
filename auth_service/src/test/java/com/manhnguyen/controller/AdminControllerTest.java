package com.manhnguyen.controller;

import com.manhnguyen.common.exception.GlobalExceptionHandler;
import com.manhnguyen.dto.UserDTO;
import com.manhnguyen.model.Role;
import com.manhnguyen.service.AdminService;
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
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class AdminControllerTest extends WebMvcTestSecuritySupport {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void listUsers_returns200() throws Exception {
        UserDTO user = UserDTO.builder()
                .id(1L)
                .name("User")
                .email("user@example.com")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        when(adminService.listUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("user@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void listUsers_returns403ForUserRole() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_returns200() throws Exception {
        UserDTO updated = UserDTO.builder()
                .id(1L)
                .name("User")
                .email("user@example.com")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        when(adminService.updateUserStatus(1L, false)).thenReturn(updated);

        mockMvc.perform(put("/api/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"active":false}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }
}
