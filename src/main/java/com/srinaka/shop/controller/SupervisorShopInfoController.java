package com.srinaka.shop.controller;

import com.srinaka.common.response.ApiResponse;
import com.srinaka.shop.dto.ShopInfoRequest;
import com.srinaka.shop.dto.ShopInfoResponse;
import com.srinaka.shop.service.ShopInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/supervisor/shop-info")
@PreAuthorize("hasRole('SUPERVISOR')")
@RequiredArgsConstructor
public class SupervisorShopInfoController {

    private final ShopInfoService shopInfoService;

    @PutMapping
    public ApiResponse<ShopInfoResponse> update(@Valid @RequestBody ShopInfoRequest request) {
        return ApiResponse.success(shopInfoService.update(request), "Shop information updated successfully.");
    }
}
