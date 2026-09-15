package com.srinaka.user.repository;

import com.srinaka.common.domain.UserRole;
import com.srinaka.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByPhone(String phone);

    Optional<User> findByLineUserId(String lineUserId);

    boolean existsByLineUserId(String lineUserId);

    List<User> findByRoleOrderByFullNameAsc(UserRole role);
}
