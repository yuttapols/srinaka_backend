package com.srinaka.auth.dto;

import com.srinaka.common.domain.UserRole;

public record MeResponse(
        String username,
        UserRole role,
        String name
) {
}
