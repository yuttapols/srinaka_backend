package com.srinaka.menu.repository;

import com.srinaka.common.domain.UserRole;

public interface MenuPermissionProjection {

    Long getMenuItemId();

    UserRole getRole();
}
