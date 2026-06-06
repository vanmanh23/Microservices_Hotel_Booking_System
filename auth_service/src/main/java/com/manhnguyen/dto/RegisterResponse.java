package com.manhnguyen.dto;

public record RegisterResponse(
        Long id,
        String email,
        String message
) {
}