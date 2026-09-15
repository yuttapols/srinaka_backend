package com.srinaka.catalog.controller;

import com.srinaka.catalog.dto.ServiceCategoryRequest;
import com.srinaka.catalog.dto.ServiceCategoryResponse;
import com.srinaka.catalog.service.ServiceCategoryService;
import com.srinaka.common.response.ApiResponse;
import jakarta.validation.Valid;
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
@RequestMapping("/api/supervisor/service-categories")
@PreAuthorize("hasRole('SUPERVISOR')")
@RequiredArgsConstructor
public class SupervisorServiceCategoryController {

    private final ServiceCategoryService serviceCategoryService;

    @GetMapping
    public ApiResponse<List<ServiceCategoryResponse>> list() {
        return ApiResponse.success(serviceCategoryService.listAll());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ServiceCategoryResponse>> create(@Valid @RequestBody ServiceCategoryRequest request) {
        ServiceCategoryResponse response = serviceCategoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Category has been created successfully."));
    }

    @PutMapping("/{id}")
    public ApiResponse<ServiceCategoryResponse> update(@PathVariable Long id, @Valid @RequestBody ServiceCategoryRequest request) {
        return ApiResponse.success(serviceCategoryService.update(id, request), "Data has been saved successfully.");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        serviceCategoryService.delete(id);
        return ApiResponse.success(null, "Category has been deleted successfully.");
    }
}
