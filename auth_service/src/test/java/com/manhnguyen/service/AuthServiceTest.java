package com.manhnguyen.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.dto.RefreshTokenRequest;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
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
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_success() {
        RegisterRequest request = new RegisterRequest("New User", "new@example.com", "password123");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });

        var response = authService.register(request);

        assertEquals(1L, response.id());
        assertEquals("new@example.com", response.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicate_email() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("Test User", "test@example.com", "password123");
        ApiException ex = assertThrows(ApiException.class, () -> authService.register(request));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    void login_success() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("user@example.com")
                .password("hashed")
                .role(Role.USER)
                .active(true)
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("user@example.com", "USER")).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken("user@example.com")).thenReturn("refresh-token");
        when(jwtUtil.getAccessExpireMs()).thenReturn(900_000L);

        var response = authService.signin("user@example.com", "password123");

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals("user@example.com", response.user().email());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_invalid_password() {
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        ApiException ex = assertThrows(ApiException.class,
                () -> authService.signin("user@example.com", "wrong"));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        assertEquals("Invalid username/password supplied", ex.getMessage());
    }

    @Test
    void refresh_token_success() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("user@example.com")
                .password("hashed")
                .role(Role.USER)
                .active(true)
                .build();

        when(jwtUtil.validateRefreshToken("valid-refresh")).thenReturn(true);
        when(jwtUtil.extractEmail("valid-refresh")).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("user@example.com", "USER")).thenReturn("new-access");
        when(jwtUtil.generateRefreshToken("user@example.com")).thenReturn("new-refresh");
        when(jwtUtil.getAccessExpireMs()).thenReturn(900_000L);

        var response = authService.refresh(new RefreshTokenRequest("valid-refresh"));

        assertEquals("new-access", response.accessToken());
        assertEquals("new-refresh", response.refreshToken());
    }

    @Test
    void refresh_token_expired() {
        when(jwtUtil.validateRefreshToken("expired-refresh")).thenReturn(false);

        ApiException ex = assertThrows(ApiException.class,
                () -> authService.refresh(new RefreshTokenRequest("expired-refresh")));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
        assertEquals("Invalid or expired refresh token", ex.getMessage());
    }

    @Test
    void refresh_token_user_not_found() {
        when(jwtUtil.validateRefreshToken("valid-refresh")).thenReturn(true);
        when(jwtUtil.extractEmail("valid-refresh")).thenReturn("missing@example.com");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> authService.refresh(new RefreshTokenRequest("valid-refresh")));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void logout_success() {
        authService.logout();
    }

    @Test
    void validateToken_success() {
        when(jwtUtil.extractEmail("valid-token")).thenReturn("user@example.com");
        when(jwtUtil.validateToken("valid-token", "user@example.com")).thenReturn(true);
        when(jwtUtil.extractTokenType("valid-token")).thenReturn("access");

        assertTrue(authService.validateToken("valid-token"));
    }

    @Test
    void validateToken_invalid() {
        when(jwtUtil.extractEmail("invalid-token")).thenThrow(new RuntimeException("invalid"));

        assertFalse(authService.validateToken("invalid-token"));
    }

    @Test
    void login_user_not_found_after_authentication() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> authService.signin("ghost@example.com", "password123"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void register_assignsUserRoleAndEncodesPassword() {
        RegisterRequest request = new RegisterRequest("New User", "new@example.com", "password123");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            assertEquals(Role.USER, user.getRole());
            assertEquals("hashed-password", user.getPassword());
            user.setId(10L);
            return user;
        });

        var response = authService.register(request);

        assertNotNull(response);
        verify(passwordEncoder).encode(eq("password123"));
    }
}
