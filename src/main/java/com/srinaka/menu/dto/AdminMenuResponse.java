package com.srinaka.menu.dto;

import com.srinaka.common.domain.UserRole;

import java.util.List;

public record AdminMenuResponse(
        Long id,
        Long parentId,
        String menuKey,
        String icon,
        String route,
        int sortOrder,
        boolean active,
        List<UserRole> roles
) {
}
