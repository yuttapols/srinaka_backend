package com.srinaka.auth.dto;

import com.srinaka.common.domain.UserRole;

import java.time.Instant;

public record MeResponse(
        String username,
        UserRole role,
        String name,
        Instant verifiedAt
) {
}
