package com.manhnguyen.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.dto.UpdateProfileRequest;
import com.manhnguyen.model.Role;
import com.manhnguyen.model.User;
import com.manhnguyen.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getProfile_success() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("user@example.com")
                .password("hashed")
                .role(Role.USER)
                .active(true)
                .build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        var profile = userService.getProfile("user@example.com");

        assertEquals("user@example.com", profile.email());
        assertEquals("Test User", profile.name());
        assertEquals(Role.USER, profile.role());
    }

    @Test
    void getProfile_user_not_found() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> userService.getProfile("missing@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void updateProfile_success() {
        User user = User.builder()
                .id(1L)
                .name("Old Name")
                .email("user@example.com")
                .password("hashed")
                .role(Role.USER)
                .active(true)
                .build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var updated = userService.updateProfile("user@example.com", new UpdateProfileRequest("New Name"));

        assertEquals("New Name", updated.name());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_user_not_found() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
                () -> userService.updateProfile("missing@example.com", new UpdateProfileRequest("Name")));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}
