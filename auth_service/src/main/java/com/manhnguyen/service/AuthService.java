package com.manhnguyen.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.dto.AuthResponse;
import com.manhnguyen.dto.RefreshTokenRequest;
import com.manhnguyen.dto.RegisterRequest;
import com.manhnguyen.dto.RegisterResponse;
import com.manhnguyen.dto.UserDTO;
import com.manhnguyen.model.Role;
import com.manhnguyen.model.User;
import com.manhnguyen.repository.UserRepository;
import com.manhnguyen.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse signin(String email, String password) {
        authenticate(email, password);
        User user = getUserByEmail(email);
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        if (!jwtUtil.validateRefreshToken(request.refreshToken())) {
            throw new ApiException("Invalid or expired refresh token", HttpStatus.UNAUTHORIZED);
        }
        String email = jwtUtil.extractEmail(request.refreshToken());
        User user = getUserByEmail(email);
        return buildAuthResponse(user);
    }

    public void logout() {
        // Stateless JWT: client discards tokens. Token blacklist can be added via Redis later.
    }

    public boolean validateToken(String token) {
        try {
            String email = jwtUtil.extractEmail(token);
            return jwtUtil.validateToken(token, email)
                    && "access".equals(jwtUtil.extractTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException("Email already exists", HttpStatus.CONFLICT);
        }
        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered: {}", savedUser.getEmail());

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                "User registered successfully"
        );
    }

    private void authenticate(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for user: {}", email);
            throw new ApiException("Invalid username/password supplied", HttpStatus.UNAUTHORIZED);
        }
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .accessToken(jwtUtil.generateToken(user.getEmail(), user.getRole().name()))
                .refreshToken(jwtUtil.generateRefreshToken(user.getEmail()))
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getAccessExpireMs() / 1000)
                .user(UserDTO.from(user))
                .build();
    }
}
