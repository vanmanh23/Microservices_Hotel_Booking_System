package com.manhnguyen.integration;

import com.manhnguyen.dto.RefreshTokenRequest;
import com.manhnguyen.model.Role;
import com.manhnguyen.repository.UserRepository;
import com.manhnguyen.service.AuthService;
import com.manhnguyen.support.AbstractPostgresIntegrationTest;
import com.manhnguyen.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
class AuthServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void registerFlow_persistsEncryptedUserWithRole() {
        var response = authService.register(TestDataFactory.registerRequest("register@example.com"));

        var persisted = userRepository.findByEmail("register@example.com").orElseThrow();
        assertEquals(response.id(), persisted.getId());
        assertEquals(Role.USER, persisted.getRole());
        assertTrue(passwordEncoder.matches("password123", persisted.getPassword()));
        assertNotEquals("password123", persisted.getPassword());
    }

    @Test
    void loginFlow_generatesJwtAndRefreshToken() {
        authService.register(TestDataFactory.registerRequest("login@example.com"));

        var response = authService.signin("login@example.com", "password123");

        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals("login@example.com", response.user().email());
        assertEquals("Bearer", response.tokenType());
    }

    @Test
    void refreshTokenFlow_validatesOldTokenAndIssuesNewTokens() {
        authService.register(TestDataFactory.registerRequest("refresh@example.com"));
        var login = authService.signin("refresh@example.com", "password123");

        var refreshed = authService.refresh(new RefreshTokenRequest(login.refreshToken()));

        assertNotNull(refreshed.accessToken());
        assertNotNull(refreshed.refreshToken());
        assertEquals("refresh@example.com", refreshed.user().email());
    }
}
