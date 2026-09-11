package com.srinaka.menu.dto;

import com.srinaka.common.domain.UserRole;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record MenuPermissionRequest(

        @NotEmpty
        List<UserRole> roles
) {
}
