package com.manhnguyen.security;

import com.manhnguyen.model.Role;
import com.manhnguyen.repository.UserRepository;
import com.manhnguyen.support.AbstractPostgresIntegrationTest;
import com.manhnguyen.support.TestDataFactory;
import com.manhnguyen.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    void protectedEndpoint_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpoint_userRole_returns403() throws Exception {
        userRepository.save(TestDataFactory.user("user@example.com", Role.USER));
        String token = jwtUtil.generateToken("user@example.com", "USER");

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_adminRole_returns200() throws Exception {
        userRepository.save(TestDataFactory.user("admin@example.com", Role.ADMIN));
        String token = jwtUtil.generateToken("admin@example.com", "ADMIN");

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void jwt_invalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void jwt_tamperedToken_returns401() throws Exception {
        userRepository.save(TestDataFactory.user("user@example.com", Role.USER));
        String token = jwtUtil.generateToken("user@example.com", "USER");
        String tampered = token.substring(0, token.length() - 6) + "tamper";

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + tampered))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void jwt_expiredToken_returns401() throws Exception {
        userRepository.save(TestDataFactory.user("user@example.com", Role.USER));
        long originalExpire = (long) ReflectionTestUtils.getField(jwtUtil, "accessExpireMs");
        ReflectionTestUtils.setField(jwtUtil, "accessExpireMs", 1L);
        String token = jwtUtil.generateToken("user@example.com", "USER");
        ReflectionTestUtils.setField(jwtUtil, "accessExpireMs", originalExpire);
        Thread.sleep(20);

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void jwt_refreshTokenCannotAccessApi_returns401() throws Exception {
        userRepository.save(TestDataFactory.user("user@example.com", Role.USER));
        String refreshToken = jwtUtil.generateRefreshToken("user@example.com");

        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void userEndpoint_withMockUser_returns200() throws Exception {
        userRepository.save(TestDataFactory.user("user@example.com", Role.USER));

        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoint_withMockAdmin_returns200() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminEndpoint_withMockUser_returns403() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }
}
