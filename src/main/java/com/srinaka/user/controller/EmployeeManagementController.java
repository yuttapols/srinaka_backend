package com.srinaka.user.controller;

import com.srinaka.common.domain.UserRole;
import com.srinaka.common.response.ApiResponse;
import com.srinaka.user.dto.CreateEmployeeRequest;
import com.srinaka.user.dto.UserResponse;
import com.srinaka.user.service.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor/employees")
@PreAuthorize("hasRole('SUPERVISOR')")
@RequiredArgsConstructor
public class EmployeeManagementController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateEmployeeRequest request) {
        UserResponse response = userRegistrationService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Employee has been created successfully."));
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> list() {
        return ApiResponse.success(userRegistrationService.listByRole(UserRole.EMPLOYEE));
    }
}
