package com.srinaka.user.dto;

import com.srinaka.common.domain.UserRole;

import java.time.Instant;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String phone,
        UserRole role,
        Instant phoneVerifiedAt,
        boolean active
) {
}
