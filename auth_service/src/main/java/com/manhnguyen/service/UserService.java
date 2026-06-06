package com.manhnguyen.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.dto.UpdateProfileRequest;
import com.manhnguyen.dto.UserDTO;
import com.manhnguyen.model.User;
import com.manhnguyen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDTO getProfile(String email) {
        return UserDTO.from(findByEmail(email));
    }

    @Transactional
    public UserDTO updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        return UserDTO.from(userRepository.save(user));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
    }
}
