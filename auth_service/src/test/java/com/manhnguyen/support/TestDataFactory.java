package com.manhnguyen.support;

import com.manhnguyen.dto.RegisterRequest;
import com.manhnguyen.model.Role;
import com.manhnguyen.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class TestDataFactory {

    private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private TestDataFactory() {
    }

    public static RegisterRequest registerRequest() {
        return new RegisterRequest("Test User", "test@example.com", "password123");
    }

    public static RegisterRequest registerRequest(String email) {
        return new RegisterRequest("Test User", email, "password123");
    }

    public static User user(String email, Role role) {
        return User.builder()
                .name("Test User")
                .email(email)
                .password(ENCODER.encode("password123"))
                .role(role)
                .active(true)
                .build();
    }

    public static User user(String email, Role role, String rawPassword) {
        return User.builder()
                .name("Test User")
                .email(email)
                .password(ENCODER.encode(rawPassword))
                .role(role)
                .active(true)
                .build();
    }
}
