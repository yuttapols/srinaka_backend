package com.srinaka.auth.dto;

import com.srinaka.common.domain.UserRole;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long expiresIn,
        UserRole role,
        String username,
        String name
) {
}
