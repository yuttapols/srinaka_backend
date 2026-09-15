package com.srinaka.catalog.controller;

import com.srinaka.catalog.dto.ServiceCategoryResponse;
import com.srinaka.catalog.dto.SpaServiceResponse;
import com.srinaka.catalog.service.ServiceCategoryService;
import com.srinaka.catalog.service.SpaServiceService;
import com.srinaka.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final SpaServiceService spaServiceService;
    private final ServiceCategoryService serviceCategoryService;

    @GetMapping("/api/services")
    public ApiResponse<List<SpaServiceResponse>> listServices() {
        return ApiResponse.success(spaServiceService.listActive());
    }

    @GetMapping("/api/services/{id}")
    public ApiResponse<SpaServiceResponse> getService(@PathVariable Long id) {
        return ApiResponse.success(spaServiceService.getActiveById(id));
    }

    @GetMapping("/api/service-categories")
    public ApiResponse<List<ServiceCategoryResponse>> listCategories() {
        return ApiResponse.success(serviceCategoryService.listActive());
    }
}
