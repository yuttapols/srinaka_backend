package com.srinaka.user.controller;

import com.srinaka.auth.dto.LineLoginRequest;
import com.srinaka.common.response.ApiResponse;
import com.srinaka.common.security.SecurityUtils;
import com.srinaka.user.dto.UpdateProfileRequest;
import com.srinaka.user.dto.UserResponse;
import com.srinaka.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PutMapping
    public ApiResponse<UserResponse> update(@Valid @RequestBody UpdateProfileRequest request) {
        UserResponse response = profileService.updateProfile(SecurityUtils.currentUserId(), request);
        return ApiResponse.success(response, "Profile updated successfully.");
    }

    @PostMapping("/link-line")
    public ApiResponse<UserResponse> linkLine(@Valid @RequestBody LineLoginRequest request) {
        UserResponse response = profileService.linkLine(SecurityUtils.currentUserId(), request);
        return ApiResponse.success(response, "LINE account linked successfully.");
    }
}
