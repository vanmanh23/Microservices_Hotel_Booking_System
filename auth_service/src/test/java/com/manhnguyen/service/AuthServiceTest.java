package com.manhnguyen.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.dto.RegisterRequest;
import com.manhnguyen.model.Role;
import com.manhnguyen.model.User;
import com.manhnguyen.repository.UserRepository;
import com.manhnguyen.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldThrowWhenEmailExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123");
        ApiException ex = assertThrows(ApiException.class, () -> authService.register(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void register_shouldSaveUser() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        RegisterRequest request = new RegisterRequest("New User", "new@example.com", "password123");
        var response = authService.register(request);

        assertEquals("new@example.com", response.email());
        assertEquals(1L, response.id());
    }
}
