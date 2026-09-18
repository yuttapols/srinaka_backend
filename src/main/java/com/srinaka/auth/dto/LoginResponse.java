package com.srinaka.auth.dto;

import com.srinaka.common.domain.UserRole;

import java.time.Instant;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UserRole role,
        String username,
        String name,
        Instant verifiedAt
) {
}
