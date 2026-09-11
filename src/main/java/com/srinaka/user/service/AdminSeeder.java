package com.srinaka.user.service;

import com.srinaka.common.domain.UserRole;
import com.srinaka.user.entity.User;
import com.srinaka.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin-seed.username}")
    private String seedUsername;

    @Value("${app.admin-seed.password:}")
    private String seedPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByUsernameIgnoreCase(seedUsername)) {
            return;
        }
        if (!StringUtils.hasText(seedPassword)) {
            throw new IllegalStateException(
                    "ADMIN_SEED_PASSWORD is not set. Set it before starting the application so the initial ADMIN account can be created.");
        }

        User admin = new User();
        admin.setUsername(seedUsername);
        admin.setPasswordHash(passwordEncoder.encode(seedPassword));
        admin.setFullName("System Administrator");
        admin.setRole(UserRole.ADMIN);
        admin.setActive(true);
        userRepository.save(admin);

        log.info("Seeded initial ADMIN account '{}' from ADMIN_SEED_PASSWORD.", seedUsername);
    }
}
