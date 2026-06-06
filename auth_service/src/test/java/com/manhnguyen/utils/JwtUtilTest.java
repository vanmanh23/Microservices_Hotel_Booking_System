package com.manhnguyen.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link JwtUtil} (JWT service layer).
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "test-secret-key-at-least-32-characters-long");
        ReflectionTestUtils.setField(jwtUtil, "accessExpireMs", 900_000L);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpireMs", 604_800_000L);
    }

    @Test
    void generateToken_success() {
        String token = jwtUtil.generateToken("user@example.com", "USER");

        assertEquals("user@example.com", jwtUtil.extractEmail(token));
        assertEquals("access", jwtUtil.extractTokenType(token));
        assertTrue(jwtUtil.validateToken(token, "user@example.com"));
    }

    @Test
    void generateRefreshToken_success() {
        String token = jwtUtil.generateRefreshToken("user@example.com");

        assertEquals("user@example.com", jwtUtil.extractEmail(token));
        assertEquals("refresh", jwtUtil.extractTokenType(token));
        assertTrue(jwtUtil.validateRefreshToken(token));
    }

    @Test
    void validateToken_rejectsWrongEmail() {
        String token = jwtUtil.generateToken("user@example.com", "USER");

        assertFalse(jwtUtil.validateToken(token, "other@example.com"));
    }

    @Test
    void validateRefreshToken_rejectsAccessToken() {
        String accessToken = jwtUtil.generateToken("user@example.com", "USER");

        assertFalse(jwtUtil.validateRefreshToken(accessToken));
    }

    @Test
    void validateToken_expired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtil, "accessExpireMs", 1L);
        String token = jwtUtil.generateToken("user@example.com", "USER");
        Thread.sleep(20);

        assertFalse(jwtUtil.validateToken(token, "user@example.com"));
    }

    @Test
    void validateRefreshToken_expired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtUtil, "refreshExpireMs", 1L);
        String token = jwtUtil.generateRefreshToken("user@example.com");
        Thread.sleep(20);

        assertFalse(jwtUtil.validateRefreshToken(token));
    }

    @Test
    void validateToken_tampered() {
        String token = jwtUtil.generateToken("user@example.com", "USER");
        String tampered = token.substring(0, token.length() - 4) + "XXXX";

        assertFalse(jwtUtil.validateToken(tampered, "user@example.com"));
    }

    @Test
    void getAccessExpireMs_returnsConfiguredValue() {
        assertEquals(900_000L, jwtUtil.getAccessExpireMs());
    }

    @Test
    void accessAndRefreshTokens_areDifferent() {
        String access = jwtUtil.generateToken("user@example.com", "USER");
        String refresh = jwtUtil.generateRefreshToken("user@example.com");

        assertNotEquals(access, refresh);
    }
}
