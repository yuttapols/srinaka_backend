package com.srinaka.menu.dto;

import com.srinaka.common.domain.UserRole;

import java.util.List;

public record MenuPermissionResponse(
        Long menuItemId,
        List<UserRole> roles
) {
}
