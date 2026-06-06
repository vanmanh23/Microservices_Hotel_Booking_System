package com.manhnguyen.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.model.Role;
import com.manhnguyen.model.User;
import com.manhnguyen.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void listUsers_success() {
        User user = User.builder()
                .id(1L)
                .name("Admin")
                .email("admin@example.com")
                .password("hashed")
                .role(Role.ADMIN)
                .active(true)
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user));

        var users = adminService.listUsers();

        assertEquals(1, users.size());
        assertEquals("admin@example.com", users.get(0).email());
    }

    @Test
    void updateUserStatus_success() {
        User user = User.builder()
                .id(1L)
                .name("User")
                .email("user@example.com")
                .password("hashed")
                .role(Role.USER)
                .active(true)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var updated = adminService.updateUserStatus(1L, false);

        assertFalse(user.isActive());
        assertEquals("user@example.com", updated.email());
    }

    @Test
    void updateUserStatus_user_not_found() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class, () -> adminService.updateUserStatus(99L, false));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}
