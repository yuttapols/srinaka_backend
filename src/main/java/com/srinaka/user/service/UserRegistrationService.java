package com.srinaka.user.service;

import com.srinaka.auth.dto.LineProfile;
import com.srinaka.auth.service.LineOAuthClient;
import com.srinaka.common.domain.UserRole;
import com.srinaka.common.error.BusinessException;
import com.srinaka.common.error.ErrorCode;
import com.srinaka.user.dto.CreateEmployeeRequest;
import com.srinaka.user.dto.CreateSupervisorRequest;
import com.srinaka.user.dto.RegisterCustomerRequest;
import com.srinaka.user.dto.RegisterCustomerViaLineRequest;
import com.srinaka.user.dto.UserResponse;
import com.srinaka.user.entity.User;
import com.srinaka.user.mapper.UserMapper;
import com.srinaka.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final LineOAuthClient lineOAuthClient;

    @Transactional
    public UserResponse registerCustomer(RegisterCustomerRequest request) {
        User customer = createUser(request.username(), request.password(), request.fullName(), request.phone(), UserRole.CUSTOMER);
        return userMapper.toResponse(customer);
    }

    @Transactional
    public UserResponse registerCustomerViaLine(RegisterCustomerViaLineRequest request) {
        LineProfile profile = lineOAuthClient.fetchProfile(request.code(), request.redirectUri());
        if (userRepository.existsByLineUserId(profile.lineUserId())) {
            throw new BusinessException(ErrorCode.LINE_ACCOUNT_ALREADY_LINKED);
        }

        User customer = createUser(request.username(), request.password(), request.fullName(), request.phone(), UserRole.CUSTOMER);
        customer.setLineUserId(profile.lineUserId());
        customer.setVerifiedAt(Instant.now());
        return userMapper.toResponse(customer);
    }

    @Transactional
    public UserResponse createSupervisor(CreateSupervisorRequest request) {
        User supervisor = createUser(request.username(), request.password(), request.fullName(), request.phone(), UserRole.SUPERVISOR);
        return userMapper.toResponse(supervisor);
    }

    @Transactional
    public UserResponse createEmployee(CreateEmployeeRequest request) {
        User employee = createUser(request.username(), request.password(), request.fullName(), request.phone(), UserRole.EMPLOYEE);
        return userMapper.toResponse(employee);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listByRole(UserRole role) {
        return userRepository.findByRoleOrderByFullNameAsc(role).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    private User createUser(String username, String rawPassword, String fullName, String phone, UserRole role) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new BusinessException(ErrorCode.USERNAME_DUPLICATE);
        }
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new BusinessException(ErrorCode.PHONE_DUPLICATE);
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);
        return user;
    }
}
