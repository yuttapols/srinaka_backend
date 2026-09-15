package com.srinaka.catalog.controller;

import com.srinaka.catalog.dto.SpaServiceRequest;
import com.srinaka.catalog.dto.SpaServiceResponse;
import com.srinaka.catalog.service.SpaServiceService;
import jakarta.validation.Valid;
import com.srinaka.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor/services")
@PreAuthorize("hasRole('SUPERVISOR')")
@RequiredArgsConstructor
public class SupervisorServiceController {

    private final SpaServiceService spaServiceService;

    @GetMapping
    public ApiResponse<List<SpaServiceResponse>> list() {
        return ApiResponse.success(spaServiceService.listAll());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SpaServiceResponse>> create(@Valid @RequestBody SpaServiceRequest request) {
        SpaServiceResponse response = spaServiceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Service has been created successfully."));
    }

    @PutMapping("/{id}")
    public ApiResponse<SpaServiceResponse> update(@PathVariable Long id, @Valid @RequestBody SpaServiceRequest request) {
        return ApiResponse.success(spaServiceService.update(id, request), "Data has been saved successfully.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        spaServiceService.delete(id);
        return ApiResponse.success(null, "Service has been deleted successfully.");
    }
}
