package com.manhnguyen.service;

import com.manhnguyen.common.exception.ApiException;
import com.manhnguyen.dto.UserDTO;
import com.manhnguyen.model.User;
import com.manhnguyen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public List<UserDTO> listUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::from)
                .toList();
    }

    @Transactional
    public UserDTO updateUserStatus(Long id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        user.setActive(active);
        return UserDTO.from(userRepository.save(user));
    }
}
