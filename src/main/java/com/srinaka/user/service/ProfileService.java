package com.srinaka.user.service;

import com.srinaka.auth.dto.LineLoginRequest;
import com.srinaka.auth.dto.LineProfile;
import com.srinaka.auth.service.LineOAuthClient;
import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import com.srinaka.user.dto.UpdateProfileRequest;
import com.srinaka.user.dto.UserResponse;
import com.srinaka.user.entity.User;
import com.srinaka.user.mapper.UserMapper;
import com.srinaka.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final LineOAuthClient lineOAuthClient;

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        boolean phoneChanged = !ObjectUtils.nullSafeEquals(user.getPhone(), request.phone());
        if (phoneChanged && request.phone() != null && userRepository.existsByPhone(request.phone())) {
            throw new BusinessException(ErrorCode.PHONE_DUPLICATE);
        }

        user.setFullName(request.fullName());
        user.setPhone(request.phone());

        return userMapper.toResponse(user);
    }

    @Transactional
    public UserResponse linkLine(Long userId, LineLoginRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        LineProfile profile = lineOAuthClient.fetchProfile(request.code(), request.redirectUri());
        if (userRepository.existsByLineUserId(profile.lineUserId())) {
            throw new BusinessException(ErrorCode.LINE_ACCOUNT_ALREADY_LINKED);
        }

        user.setLineUserId(profile.lineUserId());
        user.setVerifiedAt(Instant.now());
        return userMapper.toResponse(user);
    }
}
