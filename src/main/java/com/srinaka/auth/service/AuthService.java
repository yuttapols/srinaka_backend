package com.srinaka.auth.service;

import com.srinaka.auth.dto.ChangePasswordRequest;
import com.srinaka.auth.dto.LineLoginRequest;
import com.srinaka.auth.dto.LineProfile;
import com.srinaka.auth.dto.LoginRequest;
import com.srinaka.auth.dto.LoginResponse;
import com.srinaka.auth.dto.MeResponse;
import com.srinaka.auth.dto.RefreshRequest;
import com.srinaka.auth.dto.TokenResponse;
import com.srinaka.auth.entity.RefreshToken;
import com.srinaka.auth.repository.RefreshTokenRepository;
import com.srinaka.auth.security.TokenHasher;
import com.srinaka.common.audit.AuditActions;
import com.srinaka.common.audit.AuditLogService;
import com.srinaka.common.domain.UserRole;
import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import com.srinaka.user.entity.User;
import com.srinaka.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;
    private final LoginAttemptService loginAttemptService;
    private final LineOAuthClient lineOAuthClient;

    @Value("${app.jwt.refresh-token-ttl-seconds}")
    private long refreshTokenTtlSeconds;

    @Transactional
    public LoginResponse login(LoginRequest request, String ipAddress) {
        loginAttemptService.assertNotLocked(request.username());

        User user = userRepository.findByUsernameIgnoreCase(request.username()).orElse(null);
        if (user == null || user.getPasswordHash() == null
                || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            loginAttemptService.recordFailure(request.username());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        loginAttemptService.recordSuccess(request.username());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = issueRefreshToken(user);

        auditLogService.record(user.getUsername(), user.getRole(), AuditActions.LOGIN, ipAddress);

        return new LoginResponse(accessToken, refreshToken, jwtService.getAccessTokenTtlSeconds(),
                user.getRole(), user.getUsername(), user.getFullName(), user.getVerifiedAt());
    }

    @Transactional
    public LoginResponse loginWithLine(LineLoginRequest request, String ipAddress) {
        LineProfile profile = lineOAuthClient.fetchProfile(request.code(), request.redirectUri());

        User user = userRepository.findByLineUserId(profile.lineUserId())
                .orElseGet(() -> registerCustomerFromLine(profile));
        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = issueRefreshToken(user);

        auditLogService.record(user.getUsername(), user.getRole(), AuditActions.LOGIN, ipAddress);

        return new LoginResponse(accessToken, refreshToken, jwtService.getAccessTokenTtlSeconds(),
                user.getRole(), user.getUsername(), user.getFullName(), user.getVerifiedAt());
    }

    private User registerCustomerFromLine(LineProfile profile) {
        User user = new User();
        user.setUsername("line_" + profile.lineUserId());
        user.setPasswordHash(null);
        user.setFullName(StringUtils.hasText(profile.displayName()) ? profile.displayName() : "LINE User");
        user.setRole(UserRole.CUSTOMER);
        user.setLineUserId(profile.lineUserId());
        user.setVerifiedAt(Instant.now());
        user.setActive(true);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public TokenResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(TokenHasher.sha256(request.refreshToken()))
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID));

        if (stored.isRevoked()) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_INVALID);
        }
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        stored.setRevoked(true);
        User user = stored.getUser();

        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = issueRefreshToken(user);

        return new TokenResponse(accessToken, newRefreshToken, jwtService.getAccessTokenTtlSeconds());
    }

    @Transactional
    public void logout(String refreshToken, String username, UserRole role, String ipAddress) {
        if (StringUtils.hasText(refreshToken)) {
            refreshTokenRepository.findByTokenHash(TokenHasher.sha256(refreshToken))
                    .ifPresent(token -> token.setRevoked(true));
        }
        auditLogService.record(username, role, AuditActions.LOGOUT, ipAddress);
    }

    @Transactional(readOnly = true)
    public MeResponse getCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .map(user -> new MeResponse(user.getUsername(), user.getRole(), user.getFullName(), user.getVerifiedAt()))
                .orElse(null);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }

    private String issueRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString();

        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setTokenHash(TokenHasher.sha256(rawToken));
        entity.setExpiresAt(Instant.now().plusSeconds(refreshTokenTtlSeconds));
        refreshTokenRepository.save(entity);

        return rawToken;
    }
}
